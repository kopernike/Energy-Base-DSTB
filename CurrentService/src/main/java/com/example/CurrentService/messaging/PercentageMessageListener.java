package com.example.CurrentService.messaging;

import com.example.CurrentService.repository.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PercentageMessageListener {

    private final UsageRepository usageRepository;
    private final PercentageRepository percentageRepository;

    public PercentageMessageListener(UsageRepository usageRepository,
                                     PercentageRepository percentageRepository) {
        this.usageRepository = usageRepository;
        this.percentageRepository = percentageRepository;
    }

    @RabbitListener(queues = "prdcusrmessage")
    public void receiveUsageUpdate(String message) {
        System.out.println("RabbitMQ-Meldung empfangen: " + message);

        LocalDateTime hour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        UsageEntity usage = usageRepository.findById(hour).orElse(null);
        if (usage == null) return;

        double produced = usage.getCommunityProduced();
        double used = usage.getCommunityUsed();
        double grid = usage.getGridUsed();

        double depleted = produced == 0 ? 100 : Math.min((used / produced) * 100, 100);
        double gridPortion = used == 0 ? 0 : (grid / used) * 100;

        PercentageEntity result = new PercentageEntity();
        result.setHour(hour);
        result.setCommunityDepleted(depleted);
        result.setGridPortion(gridPortion);

        percentageRepository.save(result);
    }
}
