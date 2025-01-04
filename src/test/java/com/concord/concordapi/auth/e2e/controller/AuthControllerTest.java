package com.concord.concordapi.auth.e2e.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.concord.concordapi.auth.dto.RecoveryJwtTokenDto;
import com.concord.concordapi.auth.e2e.service.AuthServiceTest;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.service.EmailServiceTest;
import com.concord.concordapi.shared.util.UtilsMethods;
import com.concord.concordapi.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest{
    @Autowired
    private EmailServiceTest emailServiceTest;

    @Autowired
    private AuthServiceTest authServiceTest;

    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private RestTemplate restTemplate;


    @LocalServerPort
    private int port;

    private User testUser;

    @BeforeEach
    public void setup() throws Exception {
        String uuid = UtilsMethods.generateUniqueCode();
        testUser = new User(null, "user" + uuid, "user" + uuid, "user" + uuid + "@gmail.com", securityConfiguration.passwordEncoder().encode("123456"), null, null, null, null);
    }
    
    @Test
    public void testRegister(){
        authServiceTest.registerAndConfirmUser(port, testUser);
    }
    @Test
    public void testLogin() throws JsonMappingException, JsonProcessingException{
        authServiceTest.registerAndConfirmUser(port, testUser);
        authServiceTest.login(port, testUser);
    }
    @Test
    public void testForgotPassword() {
        authServiceTest.registerAndConfirmUser(port, testUser);
        String jsonRequest = "{ \"email\": \"" + testUser.getEmail() + "\" }";
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:"+port+"/api/auth/forgot-password",
                HttpMethod.POST,
                new HttpEntity<>(jsonRequest, UtilsMethods.createJsonHeaders()),
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());   
    }
    @Test
    public void testResetPassword(){
        testForgotPassword();
        String jsonRequest = "{ \"newPassword\": \"123456\" }";
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:"+port+"/api/auth/reset-password?token="+emailServiceTest.getResetCode(),
                HttpMethod.POST,
                new HttpEntity<>(jsonRequest, UtilsMethods.createJsonHeaders()),
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());   
    }

    @Test
    public void testWrongLogin() throws JsonMappingException, JsonProcessingException{
        authServiceTest.registerAndConfirmUser(port, testUser);
        try{
            ResponseEntity<RecoveryJwtTokenDto> response = authServiceTest.wrongLogin(port, testUser);
            if (!response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new RuntimeException("Wrong password logged: " + response.getStatusCode());
            }
        }catch(HttpClientErrorException e){
            if (e.getStatusCode() != HttpStatus.BAD_REQUEST) {
                throw e;
            }
        }
    }
    @Test
    public void testLoginMaxAttempts() throws JsonMappingException, JsonProcessingException{
        // authServiceTest.registerAndConfirmUser(port, testUser);
        // for(int i = 0; i<4; i++){
        //     try{
        //         ResponseEntity<RecoveryJwtTokenDto> response = authServiceTest.wrongLogin(port, testUser);
        //         if (!response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
        //             throw new RuntimeException("Wrong password logged: " + response.getStatusCode());
        //         }
        //     }catch(HttpClientErrorException e){
        //         if (e.getStatusCode() != HttpStatus.BAD_REQUEST) {
        //             throw e;
        //         }
        //     }
        // }
        // try{
        //     ResponseEntity<RecoveryJwtTokenDto> response = authServiceTest.wrongLogin(port, testUser);
        //     if (!response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
        //         throw new RuntimeException("Wrong password logged: " + response.getStatusCode());
        //     }
        // }catch(HttpClientErrorException e){
        //     if (e.getStatusCode() != HttpStatus.BAD_REQUEST) {
        //         throw e;
        //     }
        // }
        
    }
   

}
