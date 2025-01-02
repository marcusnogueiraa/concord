package com.concord.concordapi.server.e2e.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.concord.concordapi.server.e2e.responses.ServerExpectedDTO;
import com.concord.concordapi.server.e2e.responses.UserExpectedDTO;
import com.concord.concordapi.shared.UtilsMethods;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;


@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class ServerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String token;
    private int iterator=0;

    @BeforeEach
    public void setup() throws Exception {
        testUser = new User(null, "user"+iterator, "user"+iterator, "user"+iterator+"@gmail.com", securityConfiguration.passwordEncoder().encode("123456"), null, null, null);
        testUser = userRepository.save(testUser);
        String jsonRequest = "{ \"username\": \""+testUser.getUsername()+"\", \"password\": \"123456\" }";
        token = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON) // Definindo o tipo de conteúdo como JSON
                .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = UtilsMethods.extractFromResponse(token);
        iterator++;
    }

    @Test
    public void testCreateServer() throws Exception {
        ServerExpectedDTO actualResponse = UtilsMethods.createServer(mockMvc, token, testUser, "Server 1");
        ServerExpectedDTO expectedResponse = new ServerExpectedDTO(actualResponse.id(), "Server 1", new UserExpectedDTO(testUser.getName(), testUser.getUsername(), testUser.getEmail(), null), List.of());
        assertEquals(expectedResponse, actualResponse);

    }

    @Test
    public void testGetServerById() throws Exception {
        ServerExpectedDTO actualServer = UtilsMethods.createServer(mockMvc, token, testUser, "Server 2");

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/servers/"+actualServer.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();  
        response = response.replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ObjectMapper objectMapper = new ObjectMapper();
        ServerExpectedDTO actualResponse = objectMapper.readValue(response, ServerExpectedDTO.class);
        ServerExpectedDTO expectedResponse = new ServerExpectedDTO(actualResponse.id(), "Server 2", new UserExpectedDTO(testUser.getName(), testUser.getUsername(), testUser.getEmail(), null), List.of());
        assertEquals(expectedResponse, actualResponse);
        
    }

    @Test
    public void testUpdateServer() throws Exception {
        ServerExpectedDTO actualServer = UtilsMethods.createServer(mockMvc, token, testUser, "Server 3");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = "{\"name\":\"Server 3 modified\"}";
        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/servers/"+actualServer.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();  

        response = response.replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ServerExpectedDTO actualResponse = objectMapper.readValue(response, ServerExpectedDTO.class);
        ServerExpectedDTO expectedResponse = new ServerExpectedDTO(actualResponse.id(), "Server 3 modified", new UserExpectedDTO(testUser.getName(), testUser.getUsername(), testUser.getEmail(), null), List.of());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testSubscribeServer() throws Exception {
        ServerExpectedDTO actualServer = UtilsMethods.createServer(mockMvc, token, testUser, "Server 4");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/servers/"+actualServer.id()+"/subscribe/"+testUser.getUsername())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testDeleteServer() throws Exception {
        ServerExpectedDTO actualServer = UtilsMethods.createServer(mockMvc, token, testUser, "Server 5");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/servers/"+actualServer.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    
}