package com.concord.concordapi.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.concord.concordapi.auth.dto.CreateUserDto;
import com.concord.concordapi.auth.dto.ForgotPasswordRequest;
import com.concord.concordapi.auth.dto.LoginUserDto;
import com.concord.concordapi.auth.dto.RecoveryJwtTokenDto;
import com.concord.concordapi.auth.entity.UserDetailsImpl;
import com.concord.concordapi.auth.exception.UserAlreadyExistsException;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.service.EmailService;
import com.concord.concordapi.shared.service.RedisService;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;

import jakarta.mail.MessagingException;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private SecurityConfiguration securityConfiguration;

    @Mock
    private RedisService redisService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserDetailsService userDetailsService;

    private CreateUserDto createUserDto;
    private LoginUserDto loginUserDto;
    private ForgotPasswordRequest forgotPasswordRequest;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up dummy objects
        createUserDto = new CreateUserDto("John", "john123", "john.doe@email.com", "password123");
        loginUserDto = new LoginUserDto("john.doe@email.com", "password123");
        forgotPasswordRequest = new ForgotPasswordRequest("john.doe@email.com");
        user = new User(1L, "john", "john","john.doe@email.com", "password123", null, new ArrayList<>(), null, null);
        
        when(securityConfiguration.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());
    }

    @Test
    void testAuthenticateUser_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);

        String token = "jwt-token";
        when(jwtTokenService.generateToken(any(UserDetailsImpl.class))).thenReturn(token);

        RecoveryJwtTokenDto result = authService.authenticateUser(loginUserDto, "127.0.0.1");

        assertNotNull(result);
        assertEquals(token, result.token());
    }

    @Test
    void testAuthenticateUser_BadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                authService.authenticateUser(loginUserDto, "127.0.0.1"));

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail(createUserDto.email())).thenReturn(false);
        when(userRepository.existsByUsername(createUserDto.username())).thenReturn(false);

        authService.registerUser(createUserDto);

        verify(redisService, times(1)).save(anyString(), any(), anyInt());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(createUserDto.email())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                authService.registerUser(createUserDto));

        assertEquals("Email john.doe@email.com already exists.", exception.getMessage());
    }

    @Test
    void testConfirmUserRegister_Success() {
        String code = "12345678";
        User user = new User(1L, "john", "john","john.doe@email.com", "password123", null, new ArrayList<>(), null, null);
        when(redisService.find(anyString())).thenReturn(user);

        User result = authService.confirmUserRegister(code);

        assertEquals(user, result);
    }

    @Test
    void testSendForgotPassword_Success() throws MessagingException {
        when(userRepository.findByEmail(forgotPasswordRequest.email()))
                .thenReturn(Optional.of(user));

        // Mock emailService to simulate successful email sending
        doNothing().when(emailService).sendForgotPasswordEmail(anyString(), anyString());

        authService.sendForgotPassword(forgotPasswordRequest, "127.0.0.1");

        verify(redisService, times(1)).save(anyString(), any(), eq(300));
        verify(emailService, times(1)).sendForgotPasswordEmail(anyString(), anyString());
    }

    @Test
    void testResetPassword_Success() {
        String token = "reset-token";
        String newPassword = "newpassword123";
        String email = user.getEmail();

        when(redisService.find(anyString())).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        authService.resetPassword(token, newPassword, "127.0.0.1");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testIsUserTheAuthenticated_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        authService.isUserTheAuthenticated(user);
    }

    @Test
    void testIsUserTheAuthenticated_Failure() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("other@email.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthorizationDeniedException exception = assertThrows(AuthorizationDeniedException.class, () ->
                authService.isUserTheAuthenticated(user));

        assertEquals("Authenticated User doesn't have permission to perform this action.", exception.getMessage());
    }

}
