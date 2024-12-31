package com.concord.concordapi.channel.e2e.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;

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

import com.concord.concordapi.channel.dto.ChannelDTO;
import com.concord.concordapi.server.e2e.responses.ServerExpectedDTO;
import com.concord.concordapi.shared.UtilsMethods;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;



@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String token;
    private int iterator = 5;


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
    public void testCreateChannel() throws Exception {
        ChannelDTO actualResponse = createChannel(testUser, "Server 6", "Channel 1");
        ChannelDTO expectedResponse = new ChannelDTO(actualResponse.id(), "Channel 1", "channel test");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetChannelById() throws Exception {
        ChannelDTO actualChannel= createChannel(testUser, "Server 7", "Channel 2");
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/channels/"+actualChannel.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();  

        response = response.replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ObjectMapper objectMapper = new ObjectMapper();
        ChannelDTO actualResponse = objectMapper.readValue(response, ChannelDTO.class);
        ChannelDTO expectedResponse = new ChannelDTO(actualChannel.id(), "Channel 2", "channel test");
        assertEquals(expectedResponse, actualResponse);
        
    }

    @Test
    public void testUpdateChannel() throws Exception {
        ChannelDTO actualChannel= createChannel(testUser, "Server 8", "Channel 3");             
        String jsonContent = "{\"name\":\"Channel 3 modified\",\"description\":\"channel test\"}";
        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/channels/"+actualChannel.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();  
        response = response.replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ObjectMapper objectMapper = new ObjectMapper();
        ChannelDTO actualResponse = objectMapper.readValue(response, ChannelDTO.class);
        ChannelDTO expectedResponse = new ChannelDTO(actualChannel.id(), "Channel 3 modified", "channel test");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testDeleteChannel() throws Exception {
        ChannelDTO actualChannel= createChannel(testUser, "Server 8", "Channel 3");   
                
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/channels/"+actualChannel.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    public ChannelDTO createChannel(User testUser, String serverName, String channelName) throws UnsupportedEncodingException, Exception{
        ServerExpectedDTO actualServer = UtilsMethods.createServer(mockMvc, token, testUser, serverName); 
        String jsonContent = "{\"name\":\""+channelName+"\",\"serverId\":\""+actualServer.id()+"\",\"description\":\"channel test\"}";
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/channels")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();   
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, ChannelDTO.class);
    }
}