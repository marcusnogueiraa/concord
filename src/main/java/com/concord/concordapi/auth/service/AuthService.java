package com.concord.concordapi.auth.service;

import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.auth.dto.CreateUserDto;
import com.concord.concordapi.auth.dto.ForgotPasswordRequest;
import com.concord.concordapi.auth.dto.LoginUserDto;
import com.concord.concordapi.auth.dto.RecoveryJwtTokenDto;
import com.concord.concordapi.auth.entity.UserDetailsImpl;
import com.concord.concordapi.auth.exception.IncorrectCodeException;
import com.concord.concordapi.auth.exception.IncorrectTokenException;
import com.concord.concordapi.auth.exception.MaxRetryException;
import com.concord.concordapi.auth.exception.UserAlreadyExistsException;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.shared.exception.SMTPServerException;
import com.concord.concordapi.shared.service.EmailService;
import com.concord.concordapi.shared.service.RedisService;
import com.concord.concordapi.user.repository.UserRepository;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Autowired
    private RedisService redisService;

    @Autowired
    private EmailService emailService;

    private final static String CREATED_USER_CODE_KEY = "user:created_user_code:";
    private final static String LOGIN_ATTEMPTS_KEY = "user:login_attempts:";
    private final static String BLOCKED_IP_KEY = "user:blocked_ip:";
    private final static String RESET_TOKEN_KEY = "user:reset_token:";
    private static final int MAX_ATTEMPTS = 5; 
    private static final int BLOCK_TIME_IN_SECONDS = 900; 
    private final static int EMAIL_EXPIRE_TIME_IN_SECONDS = 300;

    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto, String clientIp) {
        String username = loginUserDto.username();
        String password = loginUserDto.password();
        verifyLoginAttempts(username, clientIp);
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            clearLoginAttempts(username, clientIp);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));
        } catch (BadCredentialsException exc) {
            incrementLoginAttempts(username, clientIp);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public void registerUser(CreateUserDto createUserDto){
        verifyIfAlreadyExists(createUserDto);
        User newUser = User.builder()
                .name(createUserDto.name())
                .username(createUserDto.username())
                .email(createUserDto.email())
                .password(securityConfiguration.passwordEncoder().encode(
                    createUserDto.password())
                ).build();
        sendVerificationCode(newUser);
    }

    public void confirmUserRegister(String code){
        String key = CREATED_USER_CODE_KEY + code;
        User user = (User) redisService.find(key);
        if (user == null) throw new IncorrectCodeException("Incorrect code.");
        else userRepository.save(user);
    }

    public void sendForgotPassword(@RequestBody ForgotPasswordRequest request){

         User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found with email "+request.email()));

        String token = UUID.randomUUID().toString();
        
        redisService.save(RESET_TOKEN_KEY + token, user.getUsername(), EMAIL_EXPIRE_TIME_IN_SECONDS);

        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
        try{
            emailService.sendForgotPasswordEmail(user.getEmail(), resetLink);
        } catch (Exception err) {
            throw new SMTPServerException("SMTP Server Fail");
        }
        
    }
    public void resetPassword(String token, String newPassword) {
        String username = (String) redisService.find(RESET_TOKEN_KEY + token);
        if(username == null){
            throw new IncorrectTokenException("The reset password token is malformed, invalid or expired.");
        }
        User user = userRepository.findByUsername(username).orElseThrow(()->new EntityNotFoundException("User dont find"));
        user.setPassword(securityConfiguration.passwordEncoder().encode(newPassword));
        userRepository.save(user);

        redisService.delete(RESET_TOKEN_KEY+token);
        }

    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated())
            return authentication.getName();
        return null;
    }

    private void verifyLoginAttempts(String username, String clientIp){
        String key = BLOCKED_IP_KEY + clientIp;
        boolean ipAddressIsBlocked = redisService.exists(key);
        if (ipAddressIsBlocked)
            throw new MaxRetryException("Ip Address " + clientIp + " login max retry.");
    }

    private void clearLoginAttempts(String username, String clientIp){
        String key = LOGIN_ATTEMPTS_KEY + ":" + clientIp + ":" + username;
        redisService.delete(key);
    }

    private void incrementLoginAttempts(String username, String clientIp){
        String key = LOGIN_ATTEMPTS_KEY + ":" + clientIp + ":" + username;
        redisService.saveIfDontExists(key, 0, BLOCK_TIME_IN_SECONDS);
        Long attempts = redisService.increment(key);
        
        if (attempts >= MAX_ATTEMPTS) 
            redisService.save(BLOCKED_IP_KEY + clientIp, 1, BLOCK_TIME_IN_SECONDS);
    }

    private void verifyIfAlreadyExists(CreateUserDto user){
        if (userRepository.existsByEmail(user.email())) 
            throw new UserAlreadyExistsException("Email " + user.email() + " already exists.");
        if (userRepository.existsByUsername(user.username())) 
            throw new UserAlreadyExistsException("Username " + user.username() + " already exists.");
    }

    private void sendVerificationCode(User user){
        String code = getRandomCode();

        String key = CREATED_USER_CODE_KEY + code;
        redisService.save(key, user, EMAIL_EXPIRE_TIME_IN_SECONDS);

        try {
            String email = user.getEmail();
            emailService.sendVerificationEmail(email, code);
        } catch (Exception err) {
            throw new SMTPServerException("SMTP Server Fail");
        }
    }

    private String getRandomCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder code = new StringBuilder(8);
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < 8; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }

        return code.toString();
    }

}