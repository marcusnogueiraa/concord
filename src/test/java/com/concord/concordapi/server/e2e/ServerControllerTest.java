package com.concord.concordapi.server.e2e;


import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        System.out.println(extractTokenFromResponse(token));
    }

    @Test
    public void testCreateServer() throws Exception {
        String jsonContent = "{\"name\":\"Server 1\",\"ownerId\":\"1\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/servers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isCreated());    
    }

    @Test
    public void testGetServerById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/servers/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateServer() throws Exception {
        String jsonContent = "{\"name\":\"Server 1 modificado\"}";
        mockMvc.perform(MockMvcRequestBuilders.put("/api/servers/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk());
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