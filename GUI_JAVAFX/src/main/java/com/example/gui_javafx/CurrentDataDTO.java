package com.example.gui_javafx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentDataDTO {
    private double communityDepleted;
    private double gridPortion;

    public CurrentDataDTO() {}

    public double getCommunityDepleted() {
        return communityDepleted;
    }
    public double getGridPortion() {
        return gridPortion;
    }

    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }
    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }
}
