package com.example.CurrentService.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "percentage_entity")
public class PercentageEntity {
    @Id
    private LocalDateTime hour;
    private double communityDepleted;
    private double gridPortion;

    public PercentageEntity() {}
    public PercentageEntity(LocalDateTime hour,
                            double communityDepleted,
                            double gridPortion) {
        this.hour             = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion       = gridPortion;
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }
}
