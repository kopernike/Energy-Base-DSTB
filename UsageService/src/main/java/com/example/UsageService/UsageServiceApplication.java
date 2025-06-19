package com.example.UsageService;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UsageServiceApplication {

	@Bean
	public Queue userMessageQueue() {
		return new Queue("user-message", true);
	}

	@Bean
	public Queue producerMessageQueue() {
		return new Queue("producer-message", true);
	}

	public static void main(String[] args) {
		SpringApplication.run(UsageServiceApplication.class, args);
	}

}
