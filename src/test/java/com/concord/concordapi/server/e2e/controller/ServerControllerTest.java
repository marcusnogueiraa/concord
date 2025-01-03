package com.concord.concordapi.server.e2e.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.response.ServerExpectedDTO;
import com.concord.concordapi.shared.response.UserExpectedDTO;
import com.concord.concordapi.shared.util.UtilsMethods;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;


@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServerControllerTest {
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
    private int iterator=0;

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
    public void testCreateServer() throws Exception {
        ServerExpectedDTO actualResponse = UtilsMethods.createServer(restTemplate, port, token, testUser, "Server 1");
        ServerExpectedDTO expectedResponse = new ServerExpectedDTO(actualResponse.id(), "Server 1", new UserExpectedDTO(testUser.getId(), testUser.getName(), testUser.getUsername(), testUser.getEmail(), testUser.getImagePath(), null), null, List.of());
        assertEquals(expectedResponse, actualResponse);

    }

    @Test
    public void testGetServerById() throws Exception {
        ServerExpectedDTO actualServer = UtilsMethods.createServer(restTemplate, port, token, testUser, "Server 2");
        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/servers/" + actualServer.id(),
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = responseEntity.getBody().replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ServerExpectedDTO actualResponse = objectMapper.readValue(responseBody, ServerExpectedDTO.class);
        ServerExpectedDTO expectedResponse = new ServerExpectedDTO(actualResponse.id(), "Server 2", new UserExpectedDTO(testUser.getId(), testUser.getName(), testUser.getUsername(), testUser.getEmail(), null, null), null, List.of());
        assertEquals(expectedResponse, actualResponse);
        
    }

    @Test
    public void testUpdateServer() throws Exception {
        ServerExpectedDTO actualServer = UtilsMethods.createServer(restTemplate, port, token, testUser, "Server 3");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = "{\"name\":\"Server 3 modified\"}";
        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/servers/" + actualServer.id(),
            HttpMethod.PUT,
            new HttpEntity<>(jsonContent, headers),
            String.class
        );

        String responseBody = responseEntity.getBody().replaceAll("\"createdAt\":\"[^\"]*\"", "\"createdAt\":null");
        ServerExpectedDTO actualResponse = objectMapper.readValue(responseBody, ServerExpectedDTO.class);
        ServerExpectedDTO expectedResponse = new ServerExpectedDTO(actualResponse.id(), "Server 3 modified", new UserExpectedDTO(testUser.getId(), testUser.getName(), testUser.getUsername(), testUser.getEmail(), null, null), null, List.of());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testSubscribeServer() throws Exception {
        ServerExpectedDTO actualServer = UtilsMethods.createServer(restTemplate, port, token, testUser, "Server 4");

        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/servers/" + actualServer.id()+"/subscribe/"+testUser.getId(),
            HttpMethod.POST,
            new HttpEntity<>(headers),
            String.class
        );
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteServer() throws Exception {
        ServerExpectedDTO actualServer = UtilsMethods.createServer(restTemplate, port, token, testUser, "Server 5");
        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/servers/" + actualServer.id(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }
    
}