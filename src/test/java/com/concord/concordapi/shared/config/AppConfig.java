package com.concord.concordapi.shared.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import com.concord.concordapi.shared.service.EmailService;
import com.concord.concordapi.shared.service.EmailServiceTest;

import jakarta.annotation.PreDestroy;


@Configuration
public class AppConfig {
    @Autowired
    EmailServiceTest emailServiceTest;
    

    @Bean
    @Scope("singleton")
    public EmailService emailService() {
        return emailServiceTest;
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
   
}