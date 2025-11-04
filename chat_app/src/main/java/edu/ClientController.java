package edu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @FXML
    private Button btnAddFiles;

    @FXML
    private Button btnEnter;

    @FXML
    private Tab tabClientName;

    @FXML
    private TextArea txtClientTxtArea;

    @FXML
    private TextField txtMessage;

    @FXML
    private VBox imageContainer; // for images only

    private final String SERVER_HOST = "localhost";
    private final int SERVER_PORT = 5000;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        new Thread(() -> {
            try {
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                listenForMessages();
            } catch (IOException e) {
                Platform.runLater(() -> txtClientTxtArea.appendText("Failed to connect to server.\n"));
            }
        }).start();
    }

    @FXML
    public void btnEnterOnAction(ActionEvent actionEvent) {
        String message = txtMessage.getText().trim();
        if (!message.isEmpty()) {
            try {
                out.writeUTF("TEXT:" + message);
                out.flush();

                txtClientTxtArea.appendText("You: " + message + "\n");
                txtMessage.clear();
            } catch (IOException e) {
                txtClientTxtArea.appendText("Failed to send message.\n");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void btnAddFilesOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            new Thread(() -> {
                try {
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    String encoded = Base64.getEncoder().encodeToString(fileBytes);

                    out.writeUTF("IMAGE:" + encoded);
                    out.flush();

                    // Add image to VBox for sender
                    Platform.runLater(() -> addImage(fileBytes, "You"));
                } catch (IOException e) {
                    Platform.runLater(() -> txtClientTxtArea.appendText("Failed to send image.\n"));
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void addImage(byte[] imageBytes, String sender) {
        Image image = new Image(new ByteArrayInputStream(imageBytes));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setPreserveRatio(true);

        Label lbl = new Label(sender + ":");
        imageContainer.getChildren().addAll(lbl, imageView);
    }

    private void listenForMessages() {
        try {
            while (socket.isConnected()) {
                String received = in.readUTF();

                Platform.runLater(() -> {
                    if (received.startsWith("TEXT:")) {
                        txtClientTxtArea.appendText("Friend: " + received.substring(5) + "\n");
                    } else if (received.startsWith("IMAGE:")) {
                        String base64 = received.substring(6);
                        byte[] imageBytes = Base64.getDecoder().decode(base64);
                        addImage(imageBytes, "Friend");
                    }
                });
            }
        } catch (IOException e) {
            Platform.runLater(() -> txtClientTxtArea.appendText("Disconnected from server.\n"));
        }
    }

    public void setUsername(String name) {
        tabClientName.setText(name);
    }
}
