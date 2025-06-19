package com.example.demo.repository;

import com.example.demo.model.CurrentEnergyData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PercentageRepository extends JpaRepository<CurrentEnergyData, LocalDateTime> {
}
