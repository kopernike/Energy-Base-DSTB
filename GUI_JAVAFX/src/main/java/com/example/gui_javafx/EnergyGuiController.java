package com.example.gui_javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.*;

public class EnergyGuiController {

    @FXML
    private Label communityLabel;

    @FXML
    private Label gridLabel;

    @FXML
    private Button refreshButton;

    @FXML
    private Button showDataButton;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML private TextArea outputArea;

    @FXML
    public void initialize() {
        refreshButton.setOnAction(e -> {
            // später von REST API holen
            communityLabel.setText("Community Pool: 78.54% used");
            gridLabel.setText("Grid Portion: 21.46%");
        });

        showDataButton.setOnAction(e -> {
            // später von REST API holen
            outputArea.setText("Community produced: 143.024 kWh\n" +
                    "Community used: 130.101 kWh\n" +
                    "Grid used: 14.75 kWh");
        });
    }
}
