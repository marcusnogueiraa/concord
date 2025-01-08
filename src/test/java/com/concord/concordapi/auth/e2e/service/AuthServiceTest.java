package com.concord.concordapi.auth.e2e.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.concord.concordapi.auth.dto.RecoveryJwtTokenDto;
import com.concord.concordapi.shared.service.EmailServiceTest;
import com.concord.concordapi.shared.util.UtilsMethods;
import com.concord.concordapi.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
public class AuthServiceTest {
    @Autowired
    private EmailServiceTest emailServiceTest;
    @Autowired
    private RestTemplate restTemplate;

    public void registerAndConfirmUser(int port, User testUser){
        String jsonContent = "{\"name\":\""+testUser.getName()+"\",\"username\":\""+testUser.getUsername()+"\",\"email\":\""+testUser.getEmail()+"\",\"password\":\""+"123456"+"\"}";
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/auth/register",
            HttpMethod.POST,
            new HttpEntity<>(jsonContent, UtilsMethods.createJsonHeaders()),
            String.class
        );
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        jsonContent = "{\"code\":\""+emailServiceTest.getCode()+"\"}";
        responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/api/auth/confirm",
            HttpMethod.POST,
            new HttpEntity<>(jsonContent, UtilsMethods.createJsonHeaders()),
            String.class
        );
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    public RecoveryJwtTokenDto login(int port, User testUser) throws JsonMappingException, JsonProcessingException{
        String jsonRequest = "{ \"email\": \"" + testUser.getEmail() + "\", \"password\": \"123456\" }";
        ResponseEntity<RecoveryJwtTokenDto> response = restTemplate.exchange(
                "http://localhost:"+port+"/api/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(jsonRequest, UtilsMethods.createJsonHeaders()),
                RecoveryJwtTokenDto.class);
        return response.getBody();
    }
    public ResponseEntity<RecoveryJwtTokenDto> wrongLogin(int port, User testUser) throws JsonMappingException, JsonProcessingException{
        String jsonRequest = "{ \"email\": \"" + testUser.getEmail() + "\", \"password\": \"wrongpassword\" }";
        ResponseEntity<RecoveryJwtTokenDto> response = restTemplate.exchange(
                "http://localhost:"+port+"/api/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(jsonRequest, UtilsMethods.createJsonHeaders()),
                RecoveryJwtTokenDto.class);
        return response;
    }
}
