package com.example.EnergyProducer.messaging;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class ProducerMessage {

    private final RabbitTemplate rabbit;

    public ProducerMessage(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }


    public void sendMessage(String message) {
        rabbit.convertAndSend("producer-message", message);
    }

    @Scheduled (fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void sendMessages() {
        float wert= (float) Math.random();
        System.out.println("type: PRODUCER; association: COMMUNITY; kwh: "+ wert+"; datetime" + LocalDateTime.now());
    }


}
