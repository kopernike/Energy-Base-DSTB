module com.example.gui_javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gui_javafx to javafx.fxml;
    exports com.example.gui_javafx;
}