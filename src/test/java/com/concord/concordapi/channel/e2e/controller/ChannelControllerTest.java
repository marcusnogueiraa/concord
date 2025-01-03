package com.concord.concordapi.channel.e2e.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.response.ServerExpectedDTO;
import com.concord.concordapi.shared.util.UtilsMethods;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;



@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChannelControllerTest {

    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    @LocalServerPort
    private int port;

    private User testUser;
    private String token;
    private int iterator=5;


    @BeforeEach
    public void setup() throws Exception {
        testUser = new User(null, "user" + iterator, "user" + iterator, "user" + iterator + "@gmail.com", securityConfiguration.passwordEncoder().encode("123456"), null, null, null, null);
        testUser = userRepository.save(testUser);
        String jsonRequest = "{ \"email\": \"" + testUser.getEmail() + "\", \"password\": \"123456\" }";
        
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:"+port+"/api/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(jsonRequest, UtilsMethods.createJsonHeaders()),
                String.class);

        token = UtilsMethods.extractFromResponse(response.getBody());
        iterator++;
    }

    @Test
    public void testCreateChannel() throws Exception {
        ChannelDto actualResponse = createChannel(testUser, "Server 6", "Channel 1");
        ChannelDto expectedResponse = new ChannelDto(actualResponse.id(), "Channel 1", "channel test");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetChannelById() throws Exception {
        ChannelDto actualChannel= createChannel(testUser, "Server 7", "Channel 2");

        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/channels/" + actualChannel.id(),
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            String.class
        );
        String responseBody = responseEntity.getBody().replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ObjectMapper objectMapper = new ObjectMapper();
        ChannelDto actualResponse = objectMapper.readValue(responseBody, ChannelDto.class);
        ChannelDto expectedResponse = new ChannelDto(actualChannel.id(), "Channel 2", "channel test");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testUpdateChannel() throws Exception {
        ChannelDto actualChannel= createChannel(testUser, "Server 8", "Channel 3");  

        String jsonContent = "{\"name\":\"Channel 3 modified\",\"description\":\"channel test\"}";
        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/channels/" + actualChannel.id(),
            HttpMethod.PUT,
            new HttpEntity<>(jsonContent, headers),
            String.class
        );

        String responseBody = responseEntity.getBody().replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ObjectMapper objectMapper = new ObjectMapper();
        ChannelDto actualResponse = objectMapper.readValue(responseBody, ChannelDto.class);
        ChannelDto expectedResponse = new ChannelDto(actualChannel.id(), "Channel 3 modified", "channel test");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testDeleteChannel() throws Exception {
        ChannelDto actualChannel= createChannel(testUser, "Server 8", "Channel 3");   
                
        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/channels/" + actualChannel.id(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    public ChannelDto createChannel(User testUser, String serverName, String channelName) throws UnsupportedEncodingException, Exception{
        ServerExpectedDTO actualServer = UtilsMethods.createServer(restTemplate, port, token, testUser, serverName); 

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