package com.example.UsageService.messaging;

import com.example.UsageService.dto.MessageDTO;
import com.example.UsageService.repository.UsageEntity;
import com.example.UsageService.repository.UsageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PrdcUsrMessage {

    private final RabbitTemplate rabbit;
    private final UsageRepository usageRepository;
    private final ObjectMapper objectMapper;

    public PrdcUsrMessage(RabbitTemplate rabbit, UsageRepository usageRepository) {
        this.rabbit = rabbit;
        this.usageRepository = usageRepository;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @RabbitListener(queues = "user-message")
    public void receiveUserMessage(String messageJson) {
        System.out.println("User message received: " + messageJson);

        try {
            MessageDTO dto = objectMapper.readValue(messageJson, MessageDTO.class);

            LocalDateTime hour = dto.getDatetime().withMinute(0).withSecond(0).withNano(0);
            UsageEntity usage = usageRepository.findById(hour)
                    .orElse(new UsageEntity(hour));

            double newUsed = usage.getCommunityUsed() + dto.getKwh();
            usage.setCommunityUsed(newUsed);

            if (newUsed > usage.getCommunityProduced()) {
                usage.setGridUsed(newUsed - usage.getCommunityProduced());
            }

            usageRepository.save(usage);
            System.out.println("User message processed and usage data updated.");

            // Nachricht weiterleiten an "prdcusrmessage"
            rabbit.convertAndSend("prdcusrmessage", objectMapper.writeValueAsString(dto));

        } catch (Exception e) {
            System.err.println("Error processing USER message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "producer-message")
    public void receiveProducerMessage(String messageJson) {
        System.out.println("Producer message received: " + messageJson);

        try {
            MessageDTO dto = objectMapper.readValue(messageJson, MessageDTO.class);

            LocalDateTime hour = dto.getDatetime().withMinute(0).withSecond(0).withNano(0);
            UsageEntity usage = usageRepository.findById(hour)
                    .orElse(new UsageEntity(hour));

            double newProduced = usage.getCommunityProduced() + dto.getKwh();
            usage.setCommunityProduced(newProduced);

            usageRepository.save(usage);
            System.out.println("Producer message processed and usage data updated.");

            // Nachricht weiterleiten an "prdcusrmessage"
            rabbit.convertAndSend("prdcusrmessage", objectMapper.writeValueAsString(dto));

        } catch (Exception e) {
            System.err.println("Error processing PRODUCER message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
