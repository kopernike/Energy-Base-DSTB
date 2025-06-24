package com.example.gui_javafx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricalDataDTO {

    private LocalDateTime hour;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public HistoricalDataDTO() {}

    // Getter
    public LocalDateTime getHour() {
        return hour;
    }
    public double getCommunityProduced() {
        return communityProduced;
    }
    public double getCommunityUsed() {
        return communityUsed;
    }
    public double getGridUsed() {
        return gridUsed;
    }

    // Setter (Jackson setzt so die Werte)
    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }
    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }
    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }
    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }
}
