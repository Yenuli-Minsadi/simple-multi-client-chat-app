package edu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    @FXML
    private AnchorPane ancMainDash;

    @FXML
    private Button btnLogIn;

    @FXML
    private TextField txtUsnm;

    private ServerHandler serverHandler;

    public void btnLogInOnAction(ActionEvent actionEvent) throws IOException {
        String usnm = txtUsnm.getText().trim();

        if (usnm.isEmpty()) {
            System.out.println("Please enter a valid username!");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
        AnchorPane clientPane = loader.load();

        ClientController client = loader.getController();
        client.setUsername(usnm);

        Stage clientStage = new Stage();
        clientStage.setTitle("Client"+"- "+ usnm);
        clientStage.setScene(new Scene(clientPane));
        clientStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            try {
                System.out.println("Server started...");
                serverHandler = ServerHandler.getInstance();
                serverHandler.createSocket();
            } catch (IOException e) {
                System.err.println("Failed to start server!" + e.getMessage());
            }
        }).start();
    }
}
