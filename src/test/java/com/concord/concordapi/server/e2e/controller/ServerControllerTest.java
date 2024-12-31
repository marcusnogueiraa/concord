package com.concord.concordapi.server.e2e.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.function.ServerResponse;

import com.concord.concordapi.server.dto.ServerDTO;
import com.concord.concordapi.server.e2e.responses.ServerExpectedDTO;
import com.concord.concordapi.server.e2e.responses.UserExpectedDTO;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.user.dto.UserRequestDto;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class ServerControllerTest {

    @Value("${spring.datasource.url}")
    private String url;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String token;

    @BeforeAll
    public void setup() throws Exception {
        testUser = new User(null, "marcus", "marcus", "marcus@gmail.com", securityConfiguration.passwordEncoder().encode("123456"), null, null, null);
        testUser = userRepository.save(testUser);
        String jsonRequest = "{ \"username\": \"marcus\", \"password\": \"123456\" }";
        token = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON) // Definindo o tipo de conteúdo como JSON
                .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = extractTokenFromResponse(token);
    }

    @Test
    public void testCreateServer() throws Exception {
        String jsonContent = "{\"name\":\"Server 1\",\"ownerId\":\"1\"}";
        
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/servers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();   

        response = response.replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ObjectMapper objectMapper = new ObjectMapper();
        ServerExpectedDTO actualResponse = objectMapper.readValue(response, ServerExpectedDTO.class);
        ServerExpectedDTO expectedResponse = new ServerExpectedDTO(1L, "Server 1", new UserExpectedDTO("marcus", "marcus", "marcus@gmail.com", null), List.of());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetServerById() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/servers/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();  

        response = response.replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ObjectMapper objectMapper = new ObjectMapper();
        ServerExpectedDTO actualResponse = objectMapper.readValue(response, ServerExpectedDTO.class);
        ServerExpectedDTO expectedResponse = new ServerExpectedDTO(1L, "Server 1", new UserExpectedDTO("marcus", "marcus", "marcus@gmail.com", null), List.of());
        assertEquals(expectedResponse, actualResponse);
        
    }

    @Test
    public void testUpdateServer() throws Exception {
        String jsonContent = "{\"name\":\"Server 1 modificado\"}";
        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/servers/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();  
        response = response.replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ObjectMapper objectMapper = new ObjectMapper();
        ServerExpectedDTO actualResponse = objectMapper.readValue(response, ServerExpectedDTO.class);
        ServerExpectedDTO expectedResponse = new ServerExpectedDTO(1L, "Server 1 modificado", new UserExpectedDTO("marcus", "marcus", "marcus@gmail.com", null), List.of());
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    public void testSubscribeServer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/servers/1/subscribe/"+testUser.getUsername())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    @Test
    public void testDeleteServer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/servers/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private String extractTokenFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("token").asText(); // Ajuste conforme o nome do campo no JSON
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}