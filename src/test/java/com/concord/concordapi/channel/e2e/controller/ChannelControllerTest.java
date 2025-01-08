package com.concord.concordapi.channel.e2e.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.channel.e2e.service.ChannelServiceTest;
import com.concord.concordapi.shared.util.UtilsMethods;
import com.concord.concordapi.user.entity.User;



@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChannelControllerTest{
    @Autowired
    private AuthServiceTest authServiceTest;
    @Autowired
    private ChannelServiceTest channelServiceTest;
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
        token = loginResponse.token();
    }

    @Test
    public void testCreateChannel() throws Exception {
        ChannelDto actualResponse = channelServiceTest.createChannel(testUser, "Server 6", "Channel 1", token, port);
        ChannelDto expectedResponse = new ChannelDto(actualResponse.id(), "Channel 1", "channel test");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetChannelById() throws Exception {
        ChannelDto actualChannel= channelServiceTest.createChannel(testUser, "Server 7", "Channel 2", token, port);

        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<ChannelDto> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/channels/" + actualChannel.id(),
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            ChannelDto.class
        );
        ChannelDto actualResponse = responseEntity.getBody();
        ChannelDto expectedResponse = new ChannelDto(actualChannel.id(), "Channel 2", "channel test");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testUpdateChannel() throws Exception {
        ChannelDto actualChannel= channelServiceTest.createChannel(testUser, "Server 8", "Channel 3", token, port);  

        String jsonContent = "{\"name\":\"Channel 3 modified\",\"description\":\"channel test\"}";
        HttpHeaders headers = UtilsMethods.createJsonHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<ChannelDto> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/channels/" + actualChannel.id(),
            HttpMethod.PUT,
            new HttpEntity<>(jsonContent, headers),
            ChannelDto.class
        );
        ChannelDto actualResponse = responseEntity.getBody();
        ChannelDto expectedResponse = new ChannelDto(actualChannel.id(), "Channel 3 modified", "channel test");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testDeleteChannel() throws Exception {
        ChannelDto actualChannel= channelServiceTest.createChannel(testUser, "Server 8", "Channel 3", token, port);   
                
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

    
}