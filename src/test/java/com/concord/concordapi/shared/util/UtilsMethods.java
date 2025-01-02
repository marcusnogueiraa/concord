package com.concord.concordapi.shared.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.concord.concordapi.shared.response.ServerExpectedDTO;
import com.concord.concordapi.user.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UtilsMethods {
    public static String extractFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("token").asText(); // Ajuste conforme o nome do campo no JSON
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ServerExpectedDTO createServer(RestTemplate restTemplate, int port, String token, User testUser, String serverName) throws UnsupportedEncodingException, Exception{
        String url = "http://localhost:"+port+"/api/servers";
        String jsonContent = "{\"name\":\"" + serverName + "\",\"ownerId\":\"" + testUser.getId() + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonContent, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody().replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, ServerExpectedDTO.class);
        } else {
            throw new RuntimeException("Failed to create server: " + responseEntity.getStatusCode());
    }
    }
    public static HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
