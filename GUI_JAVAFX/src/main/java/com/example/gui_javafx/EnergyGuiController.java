/* package com.example.gui_javafx;

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
            communityLabel.setText("Community Pool: 78.54% used");
            gridLabel.setText("Grid Portion: 21.46%");
        });

        showDataButton.setOnAction(e -> {
            outputArea.setText("Community produced: 143.024 kWh\n" +
                    "Community used: 130.101 kWh\n" +
                    "Grid used: 14.75 kWh");
        });
    }
}*/

package com.example.gui_javafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.util.Scanner;

public class EnergyGuiController {

    @FXML private Label communityLabel;
    @FXML private Label gridLabel;
    @FXML private Button refreshButton;
    @FXML private Button showDataButton;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextArea outputArea;

    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        refreshButton.setOnAction(e -> fetchCurrentData());
        showDataButton.setOnAction(e -> fetchHistoricalData());
    }

    private void fetchCurrentData() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/energy/current"))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                    .thenApply(HttpResponse::body)
                    .thenAccept(stream -> {
                        String json = convertStreamToString(stream);

                        double communityUsed = Double.parseDouble(getJsonValue(json, "communityUsed"));
                        double gridPortion = Double.parseDouble(getJsonValue(json, "gridPortion"));

                        Platform.runLater(() -> {
                            communityLabel.setText("Community Pool: " + communityUsed + "% used");
                            gridLabel.setText("Grid Portion: " + gridPortion + "%");
                        });
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchHistoricalData() {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();

        if (start == null || end == null) {
            outputArea.setText("Bitte Start- und Enddatum auswählen.");
            return;
        }

        String startStr = start + "T00:00";
        String endStr = end + "T23:59";

        try {
            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", startStr, endStr);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                    .thenApply(HttpResponse::body)
                    .thenAccept(stream -> {
                        String json = convertStreamToString(stream);

                        // Manuell splitten – sehr basic
                        String[] entries = json.replace("[", "")
                                .replace("]", "")
                                .split("\\},\\{");

                        StringBuilder sb = new StringBuilder();

                        for (String entry : entries) {
                            entry = entry.replace("{", "").replace("}", "").replace("\"", "");
                            String[] fields = entry.split(",");

                            String hour = getField(fields, "hour");
                            String produced = getField(fields, "communityProduced");
                            String used = getField(fields, "communityUsed");
                            String grid = getField(fields, "gridUsed");

                            sb.append("[").append(hour).append("] ")
                                    .append("Produced: ").append(produced).append(" kWh, ")
                                    .append("Used: ").append(used).append(" kWh, ")
                                    .append("Grid: ").append(grid).append(" kWh\n");
                        }

                        Platform.runLater(() -> outputArea.setText(sb.toString()));
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📦 Hilfsmethoden für JSON Parsing ohne Gson
    private String convertStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private String getJsonValue(String json, String key) {
        String[] parts = json.replace("{", "").replace("}", "").replace("\"", "").split(",");
        for (String part : parts) {
            String[] kv = part.split(":");
            if (kv[0].trim().equals(key)) {
                return kv[1].trim();
            }
        }
        return "0.0";
    }

    private String getField(String[] fields, String key) {
        for (String field : fields) {
            String[] kv = field.split(":");
            if (kv.length == 2 && kv[0].trim().equals(key)) {
                return kv[1].trim();
            }
        }
        return "-";
    }
}

