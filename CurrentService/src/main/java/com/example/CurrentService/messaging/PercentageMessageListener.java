package com.example.CurrentService.messaging;

import com.example.CurrentService.dto.PercentageUpdateDTO;
import com.example.CurrentService.service.CurrentPercentageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PercentageMessageListener {

    private final CurrentPercentageService percentageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PercentageMessageListener(CurrentPercentageService percentageService) {
        this.percentageService = percentageService;
    }

    @RabbitListener(queues = "update-percentage")
    public void handleMessage(String messageJson) {
        try {
            PercentageUpdateDTO dto = objectMapper.readValue(messageJson, PercentageUpdateDTO.class);
            percentageService.updatePercentageForHour(dto.getHour());
        } catch (Exception e) {
            System.err.println("Failed to process percentage message: " + e.getMessage());
        }
    }
}
