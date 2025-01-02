package com.concord.concordapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConcordapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcordapiApplication.class, args);
	}

}
