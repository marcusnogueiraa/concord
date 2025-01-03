package com.concord.concordapi.auth.e2e.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.service.EmailService;
import com.concord.concordapi.shared.util.UtilsMethods;
import com.concord.concordapi.user.entity.User;

// @ActiveProfiles("test")
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @AutoConfigureMockMvc
// public class AuthControllerTest {
//     @MockBean
//     private EmailService emailService;
//     @Autowired
//     private SecurityConfiguration securityConfiguration;
//     @Autowired
//     private RestTemplate restTemplate;
//     @LocalServerPort
//     private int port;

//     private User testUser;
//     private int iterator=5;

//     @BeforeEach
//     public void setup() throws Exception {
//         testUser = new User(null, "user" + iterator, "user" + iterator, "user" + iterator + "@gmail.com", securityConfiguration.passwordEncoder().encode("123456"), null, null, null, null);
//         iterator++;
//     }
    
//     @Test
//     public void testRegister() throws Exception {
//         String jsonContent = "{\"name\":\""+testUser.getName()+"\",\"username\":\""+testUser.getUsername()+"\",\"email\":\""+testUser.getEmail()+"\",\"password\":\""+"123456"+"\"}";
//         System.out.println(jsonContent);
//         ResponseEntity<String> responseEntity = restTemplate.exchange(
//             "http://localhost:" + port + "/api/auth/register",
//             HttpMethod.POST,
//             new HttpEntity<>(jsonContent, UtilsMethods.createJsonHeaders()),
//             String.class
//         );
//         assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//     }


// }
