module com.example.gui_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    opens com.example.gui_javafx to javafx.fxml;
    exports com.example.gui_javafx;
}