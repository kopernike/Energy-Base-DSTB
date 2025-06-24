package com.example.gui_javafx;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EnergyGuiController {

    @FXML private Label communityLabel;
    @FXML private Label gridLabel;
    @FXML private Button refreshButton;
    @FXML private Button showDataButton;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextArea outputArea;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @FXML
    public void initialize() {
        refreshButton.setOnAction(e -> fetchCurrentData());
        showDataButton.setOnAction(e -> fetchHistoricalData());
    }

    private void fetchCurrentData() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    // Port 8081 falls deine REST-API auf 8081 läuft
                    .uri(new URI("http://localhost:8081/energy/current"))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            parseAndDisplayCurrent(response.body());
                        } else {
                            Platform.runLater(() ->
                                    outputArea.setText(
                                            "Fehler beim Laden aktueller Daten: HTTP "
                                                    + response.statusCode())
                            );
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() ->
                    outputArea.setText("Fehler beim Senden der Anfrage.")
            );
        }
    }

    private void parseAndDisplayCurrent(String json) {
        try {
            CurrentDataDTO dto = mapper.readValue(json, CurrentDataDTO.class);
            Platform.runLater(() -> {
                communityLabel.setText(
                        String.format("Community Pool: %.2f%% used", dto.getCommunityDepleted()));
                gridLabel.setText(
                        String.format("Grid Portion: %.2f%%", dto.getGridPortion()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() ->
                    outputArea.setText("Fehler beim Verarbeiten der aktuellen Daten.")
            );
        }
    }

    private void fetchHistoricalData() {
        LocalDate start = startDate.getValue();
        LocalDate end   = endDate.getValue();
        if (start == null || end == null) {
            outputArea.setText("Bitte Start- und Enddatum auswählen.");
            return;
        }

        String startStr = start + "T00:00";
        String endStr   = end   + "T23:59";
        String url = String.format(
                "http://localhost:8081/energy/historical?start=%s&end=%s",
                startStr, endStr
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            displayHistorical(response.body());
                        } else {
                            Platform.runLater(() ->
                                    outputArea.setText(
                                            "Fehler beim Laden historischer Daten: HTTP "
                                                    + response.statusCode())
                            );
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() ->
                    outputArea.setText("Fehler beim Senden der Anfrage.")
            );
        }
    }

    private void displayHistorical(String json) {
        try {
            List<HistoricalDataDTO> list = mapper.readValue(
                    json,
                    new TypeReference<List<HistoricalDataDTO>>() {}
            );

            StringBuilder sb = new StringBuilder();
            for (HistoricalDataDTO dto : list) {
                sb.append("[")
                        .append(dto.getHour())
                        .append("] Produced: ")
                        .append(dto.getCommunityProduced())
                        .append(" kWh, Used: ")
                        .append(dto.getCommunityUsed())
                        .append(" kWh, Grid: ")
                        .append(dto.getGridUsed())
                        .append(" kWh\n");
            }
            Platform.runLater(() -> outputArea.setText(sb.toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() ->
                    outputArea.setText("Fehler beim Verarbeiten historischer Daten.")
            );
        }
    }
}
