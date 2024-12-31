package com.concord.concordapi.shared;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.concord.concordapi.server.e2e.responses.ServerExpectedDTO;
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
    
    public static ServerExpectedDTO createServer(MockMvc mockMvc, String token, User testUser, String serverName) throws UnsupportedEncodingException, Exception{
        String jsonContent = "{\"name\":\""+serverName+"\",\"ownerId\":\""+testUser.getId()+"\"}";
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/servers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();  
        response = response.replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, ServerExpectedDTO.class);
    }
}
