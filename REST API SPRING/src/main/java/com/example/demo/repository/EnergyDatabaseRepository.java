package com.example.demo.repository;

import com.example.demo.model.CurrentEnergyData;
import com.example.demo.model.EnergyUsageEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EnergyDatabaseRepository
        extends JpaRepository<EnergyUsageEntry, LocalDateTime> {

    // Custom-Methode für den Zeitraum
    List<EnergyUsageEntry> findByHourBetween(LocalDateTime start, LocalDateTime end);
}
