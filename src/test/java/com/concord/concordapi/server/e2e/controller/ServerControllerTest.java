package com.concord.concordapi.server.e2e.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

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

import com.concord.concordapi.auth.dto.RecoveryJwtTokenDto;
import com.concord.concordapi.auth.e2e.service.AuthServiceTest;
import com.concord.concordapi.server.dto.response.ServerDto;
import com.concord.concordapi.server.e2e.service.ServerServiceTest;
import com.concord.concordapi.shared.util.UtilsMethods;
import com.concord.concordapi.user.dto.response.UserDto;
import com.concord.concordapi.user.entity.User;


@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServerControllerTest{
    @Autowired
    private ServerServiceTest serverServiceTest;
    @Autowired
    private AuthServiceTest authServiceTest;
    @Autowired
    private RestTemplate restTemplate;
    @LocalServerPort
    private int port;

    private User testUser;
    private String token;

    @BeforeEach
    public void setup() throws Exception {
        String uuid = UtilsMethods.generateUniqueCode();
        testUser = new User(null, "user" + uuid, "user" + uuid, "user" + uuid + "@gmail.com", "123456", null, null, null, null);
        authServiceTest.registerAndConfirmUser(port, testUser);
        RecoveryJwtTokenDto loginResponse = authServiceTest.login(port, testUser);
        testUser.setId(loginResponse.user().id());
        testUser.setCreatedAt(loginResponse.user().createdAt());
        token = loginResponse.token();
    }

    @Test
    public void testCreateServer() throws Exception {
        ServerDto actualResponse = serverServiceTest.createServer(port, token, testUser, "Server 1");
        ServerDto expectedResponse = new ServerDto(actualResponse.id(), "Server 1", null, new UserDto(testUser.getId(), testUser.getName(), testUser.getUsername(), null, testUser.getEmail(), testUser.getCreatedAt()), List.of());
        assertEquals(expectedResponse, actualResponse);

    }

    @Test
    public void testGetServerById() throws Exception {
        ServerDto actualServer = serverServiceTest.createServer(port, token, testUser, "Server 2");
        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<ServerDto> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/servers/" + actualServer.id(),
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            ServerDto.class
        );
       
        ServerDto actualResponse = responseEntity.getBody();
        ServerDto expectedResponse = new ServerDto(actualResponse.id(), "Server 2", null, new UserDto(testUser.getId(), testUser.getName(), testUser.getUsername(), null, testUser.getEmail(), testUser.getCreatedAt()), List.of());
        assertEquals(expectedResponse, actualResponse);
        
    }

    @Test
    public void testUpdateServer() throws Exception {
        ServerDto actualServer = serverServiceTest.createServer(port, token, testUser, "Server 3");
        String jsonContent = "{\"name\":\"Server 3 modified\"}";
        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<ServerDto> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/servers/" + actualServer.id(),
            HttpMethod.PUT,
            new HttpEntity<>(jsonContent, headers),
            ServerDto.class
        );
        ServerDto actualResponse = responseEntity.getBody();
        ServerDto expectedResponse = new ServerDto(actualResponse.id(), "Server 3 modified", null, new UserDto(testUser.getId(), testUser.getName(), testUser.getUsername(), null, testUser.getEmail(), testUser.getCreatedAt()), List.of());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testSubscribeServer() throws Exception {
        ServerDto actualServer = serverServiceTest.createServer(port, token, testUser, "Server 4");
        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/servers/" + actualServer.id()+"/subscribe/"+testUser.getUsername(),
            HttpMethod.POST,
            new HttpEntity<>(headers),
            String.class
        );
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteServer() throws Exception {
        ServerDto actualServer = serverServiceTest.createServer(port, token, testUser, "Server 5");
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