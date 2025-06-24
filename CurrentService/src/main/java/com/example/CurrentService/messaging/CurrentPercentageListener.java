package com.example.CurrentService.messaging;

import com.example.CurrentService.dto.MessageDTO;
import com.example.CurrentService.repository.PercentageEntity;
import com.example.CurrentService.repository.PercentageRepository;
import com.example.CurrentService.repository.UsageEntity;
import com.example.CurrentService.repository.UsageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CurrentPercentageListener {

    private final UsageRepository usageRepo;
    private final PercentageRepository pctRepo;
    private final ObjectMapper mapper;

    public CurrentPercentageListener(UsageRepository usageRepo,
                                     PercentageRepository pctRepo) {
        this.usageRepo = usageRepo;
        this.pctRepo   = pctRepo;
        this.mapper    = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @RabbitListener(queues = "prdcusrmessage")
    public void onMessage(String json) {
        try {
            // 1) JSON → DTO
            MessageDTO dto = mapper.readValue(json, MessageDTO.class);

            // 2) Runden auf volle Stunde
            LocalDateTime hour = dto.getDatetime()
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            // 3) UsageEntity holen (oder neu anlegen, falls noch nicht vorhanden)
            UsageEntity usage = usageRepo.findById(hour)
                    .orElse(new UsageEntity(hour));

            double used     = usage.getCommunityUsed();
            double produced = usage.getCommunityProduced();
            double grid     = usage.getGridUsed();

            // 4) Prozentwerte berechnen
            double communityDepleted = produced <= 0
                    ? 0.0
                    : Math.min(100.0, used / produced * 100.0);

            double totalConsumed = used + grid;
            double gridPortion   = totalConsumed <= 0
                    ? 0.0
                    : (grid / totalConsumed) * 100.0;

            // 5) PercentageEntity holen oder anlegen
            PercentageEntity pct = pctRepo.findById(hour)
                    .orElse(new PercentageEntity(hour, communityDepleted, gridPortion));

            pct.setCommunityDepleted(communityDepleted);
            pct.setGridPortion(gridPortion);
            pctRepo.save(pct);

            System.out.printf(
                    ">> [%s] depleted=%.2f%%, grid=%.2f%%%n",
                    hour, communityDepleted, gridPortion
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
