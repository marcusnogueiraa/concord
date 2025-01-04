package com.concord.concordapi.server.e2e.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.concord.concordapi.server.dto.response.ServerDto;
import com.concord.concordapi.user.entity.User;

@Component
public class ServerServiceTest {
    @Autowired
    private RestTemplate restTemplate;
    
    public ServerDto createServer(int port, String token, User testUser, String serverName) throws UnsupportedEncodingException, Exception{
        String url = "http://localhost:"+port+"/api/servers";
        String jsonContent = "{\"name\":\"" + serverName + "\",\"ownerId\":\"" + testUser.getId() + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonContent, headers);
        ResponseEntity<ServerDto> responseEntity = restTemplate.postForEntity(url, requestEntity, ServerDto.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        return responseEntity.getBody();

    }
    
}