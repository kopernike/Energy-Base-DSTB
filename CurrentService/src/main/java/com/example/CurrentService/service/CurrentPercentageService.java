package com.example.CurrentService.service;

import com.example.CurrentService.repository.PercentageEntity;
import com.example.CurrentService.repository.PercentageRepository;
import com.example.UsageService.repository.UsageEntity;
import com.example.UsageService.repository.UsageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CurrentPercentageService {

    private final UsageRepository usageRepository;
    private final PercentageRepository percentageRepository;

    public CurrentPercentageService(UsageRepository usageRepository, PercentageRepository percentageRepository) {
        this.usageRepository = usageRepository;
        this.percentageRepository = percentageRepository;
    }

    public void updatePercentageForHour(LocalDateTime hour) {
        UsageEntity usage = usageRepository.findById(hour).orElse(null);
        if (usage == null) return;

        double used = usage.getCommunityUsed();
        double grid = usage.getGridUsed();

        double gridPortion = (used > 0) ? (grid / used) * 100.0 : 0.0;

        PercentageEntity entity = new PercentageEntity(hour);
        entity.setCommunityDepleted(100.0);
        entity.setGridPortion(Math.round(gridPortion * 100.0) / 100.0);

        percentageRepository.save(entity);
        System.out.println("Percentage updated for hour: " + hour);
    }
}
