package com.example.CurrentService;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CurrentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrentServiceApplication.class, args);
	}

	@Bean
	public Queue prdcUsrQueue() {
		return new Queue("prdcusrmessage", true);
	}
}
