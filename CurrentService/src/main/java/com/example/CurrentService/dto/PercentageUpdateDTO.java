package com.example.CurrentService.dto;

import java.time.LocalDateTime;

public class PercentageUpdateDTO {
    private LocalDateTime hour;

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }
}