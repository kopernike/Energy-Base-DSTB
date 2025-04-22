package com.example.gui_javafx;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

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

    @FXML
    private TextArea outputArea;

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

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

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        CurrentEnergyData data = gson.fromJson(response, CurrentEnergyData.class);
                        Platform.runLater(() -> {
                            communityLabel.setText("Community Pool: " + data.communityUsed + "% used");
                            gridLabel.setText("Grid Portion: " + data.gridPortion + "%");
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

        String startStr = start.atStartOfDay().toString();
        String endStr = end.atTime(23, 59).toString();

        try {
            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", startStr, endStr);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        Type listType = new TypeToken<List<EnergyUsageEntry>>(){}.getType();
                        List<EnergyUsageEntry> list = gson.fromJson(response, listType);

                        StringBuilder sb = new StringBuilder();
                        for (EnergyUsageEntry entry : list) {
                            sb.append("[").append(entry.hour).append("] ")
                                    .append("Produced: ").append(entry.communityProduced).append(" kWh, ")
                                    .append("Used: ").append(entry.communityUsed).append(" kWh, ")
                                    .append("Grid: ").append(entry.gridUsed).append(" kWh\n");
                        }

                        Platform.runLater(() -> outputArea.setText(sb.toString()));
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Klassen für JSON-Mapping
    private static class CurrentEnergyData {
        double communityUsed;
        double gridPortion;
    }

    private static class EnergyUsageEntry {
        String hour;
        double communityProduced;
        double communityUsed;
        double gridUsed;
    }
}
