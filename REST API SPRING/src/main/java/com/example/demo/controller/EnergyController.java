package com.example.demo.controller;

import com.example.demo.model.CurrentEnergyData;
import com.example.demo.model.EnergyUsageEntry;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    @GetMapping("/current")
    public CurrentEnergyData getCurrent() {
        return new CurrentEnergyData(78.54, 21.46);
    }

    @GetMapping("/historical")
    public List<EnergyUsageEntry> getHistorical(@RequestParam String start, @RequestParam String end) {
        return List.of(
                new EnergyUsageEntry("2025-01-10T14:00", 143.024, 130.101, 14.75),
                new EnergyUsageEntry("2025-01-10T13:00", 140.0, 135.0, 10.0)
        );
    }
}
