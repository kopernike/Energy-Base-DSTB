package com.example.EnergyUser.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
@Service
public class UserMessage {

    private final RabbitTemplate rabbit;

    public UserMessage(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void sendMassage(String message) {
        rabbit.convertAndSend("user-message", message);
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void doSomething() {
        float wert= (float) Math.random();
        sendMassage("{"
                + "\"type\":\"USER\","
                + "\"association\":\"COMMUNITY\","
                + "\"kwh\":" + wert + ","
                + "\"datetime\":\"" + LocalDateTime.now() + "\""
                + "}");
    }
}
