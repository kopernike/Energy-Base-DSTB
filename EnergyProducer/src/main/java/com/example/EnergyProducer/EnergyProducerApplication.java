package com.example.EnergyProducer;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class EnergyProducerApplication {


	public static void main(String[] args) {
		SpringApplication.run(EnergyProducerApplication.class, args);
	}

	@Bean
	public Queue producerQueue() {
		return new Queue("producer-message", true);

	}


}
