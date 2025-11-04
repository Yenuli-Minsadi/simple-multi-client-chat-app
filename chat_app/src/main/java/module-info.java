module edu.chat_app {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu to javafx.fxml;
    exports edu;
}