package com.concord.concordapi.channel.e2e.service;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.server.dto.response.ServerDto;
import com.concord.concordapi.server.e2e.service.ServerServiceTest;
import com.concord.concordapi.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ChannelServiceTest {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServerServiceTest serverServiceTest;

    public ChannelDto createChannel(User testUser, String serverName, String channelName, String token, int port) throws UnsupportedEncodingException, Exception{
        ServerDto actualServer = serverServiceTest.createServer(port, token, testUser, serverName); 

        String jsonContent = "{\"name\":\""+channelName+"\",\"serverId\":\""+actualServer.id()+"\",\"description\":\"channel test\"}";     
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonContent, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:"+port+"/api/channels", requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody().replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, ChannelDto.class);
        } else {
            throw new RuntimeException("Failed to create server: " + responseEntity.getStatusCode());
        }
    }
    
}
