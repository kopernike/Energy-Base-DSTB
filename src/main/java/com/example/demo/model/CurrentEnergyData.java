package com.example.demo.model;

public class CurrentEnergyData {
    private double communityUsed;
    private double gridPortion;

    public CurrentEnergyData(double communityUsed, double gridPortion) {
        this.communityUsed = communityUsed;
        this.gridPortion = gridPortion;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public double getGridPortion() {
        return gridPortion;
    }
}
