module com.example.gui_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;


    opens com.example.gui_javafx to javafx.fxml;
    exports com.example.gui_javafx;
}