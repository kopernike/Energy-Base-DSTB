package com.example.UsageService.messaging;

import com.example.UsageService.dto.MessageDTO;
import com.example.UsageService.repository.UsageEntity;
import com.example.UsageService.repository.UsageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PrdcUsrMessage {

    private final UsageRepository usageRepo;
    private final RabbitTemplate rabbit;
    private final ObjectMapper objectMapper;

    public PrdcUsrMessage(RabbitTemplate rabbit,
                          UsageRepository usageRepo) {
        this.rabbit     = rabbit;
        this.usageRepo  = usageRepo;
        // Jackson-Mapper für ISO-Datumsstrings:
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    /**
     * Hört auf beide Queues und verarbeitet Producer- und User-Nachrichten
     * sequenziell in einem einzigen Thread.
     */
    @RabbitListener(queues = {"producer-message", "user-message"})
    public void receive(String messageJson) {
        try {
            // 1) JSON → DTO
            MessageDTO dto = objectMapper.readValue(messageJson, MessageDTO.class);

            // 2) Minute auf volle Stunde runden
            LocalDateTime hour = dto.getDatetime()
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            // 3) UsageEntity holen oder neu anlegen
            UsageEntity u = usageRepo.findById(hour)
                    .orElse(new UsageEntity(hour));

            // 4) Je nach Typ aufaddieren
            if ("PRODUCER".equals(dto.getType())) {
                u.setCommunityProduced(u.getCommunityProduced() + dto.getKwh());
            } else {
                u.setCommunityUsed(u.getCommunityUsed() + dto.getKwh());
            }

            // 5) Grid-Usage immer neu berechnen
            double used     = u.getCommunityUsed();
            double produced = u.getCommunityProduced();
            u.setGridUsed(Math.max(0.0, used - produced));

            // 6) In DB speichern (erstes Mal ein Insert, danach Updates)
            usageRepo.save(u);

            // 7) Unverändertes DTO weiterleiten an CurrentPercentageService
            rabbit.convertAndSend("prdcusrmessage",
                    objectMapper.writeValueAsString(dto));
        }
        catch (Exception e) {
            // Fehler protokollieren, aber Listener am Laufen halten
            System.err.println("Fehler beim Verarbeiten von: " + messageJson);
            e.printStackTrace();
        }
    }
}
