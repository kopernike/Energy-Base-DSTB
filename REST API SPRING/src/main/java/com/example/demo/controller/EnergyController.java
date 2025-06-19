package com.example.demo.controller;

import com.example.demo.model.CurrentEnergyData;
import com.example.demo.model.EnergyUsageEntry;
import com.example.demo.repository.EnergyDatabaseRepository;
import com.example.demo.repository.PercentageRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/energy")
@CrossOrigin  // wichtig für die Verbindung zur GUI
public class EnergyController {

    private final EnergyDatabaseRepository usageRepo;
    private final PercentageRepository percentageRepo;

    public EnergyController(EnergyDatabaseRepository usageRepo, PercentageRepository percentageRepo) {
        this.usageRepo = usageRepo;
        this.percentageRepo = percentageRepo;
    }

    // GET /energy/current
    @GetMapping("/current")
    public CurrentEnergyData getCurrentPercentage() {
        // Runde aktuelle Zeit auf volle Stunde
        LocalDateTime currentHour = LocalDateTime.now()
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        return percentageRepo.findById(currentHour)
                .orElse(null); // oder: throw new ResponseStatusException(...)
    }

    // GET /energy/historical?start=...&end=...
    @GetMapping("/historical")
    public List<EnergyUsageEntry> getHistoricalUsage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return usageRepo.findByHourBetween(start, end);
    }
}

