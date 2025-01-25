package com.concord.concordapi.auth.service;

import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.mapper.UserMapper;
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
    private final static String FORGOT_PASSWORD_ATTEMPTS_KEY = "user:forgot_password_attempts:";
    private final static String BLOCKED_LOGIN_IP_KEY = "user:blocked_login_ip:";
    private final static String BLOCKED_FORGOT_PASSWORD_IP_KEY = "user:blocked_forgot_password_ip:";
    private final static String RESET_TOKEN_KEY = "user:reset_token:";
    private static final int MAX_ATTEMPTS = 5; 
    private static final int BLOCK_TIME_IN_SECONDS = 900; 
    private final static int EMAIL_EXPIRE_TIME_IN_SECONDS = 300;


    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto, String clientIp) {
        String email = loginUserDto.email();
        String password = loginUserDto.password();
        verifyLoginAttempts(clientIp);
        try {
            
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            clearLoginAttempts(email, clientIp);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails), UserMapper.toDto(userDetails.getUser()));
        } catch (BadCredentialsException exc) {
            incrementLoginAttempts(email, clientIp);
            throw new BadCredentialsException("Invalid email or password");
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

    public User confirmUserRegister(String code){
        String key = CREATED_USER_CODE_KEY + code;
        User user = (User) redisService.find(key);
        if (user == null) throw new IncorrectCodeException("Incorrect code.");
        else userRepository.save(user);
        return user;
    }

    public Boolean validateToken(String token){
        return jwtTokenService.isTokenValid(token);
    }

    public void sendForgotPassword(@RequestBody ForgotPasswordRequest request, String clientIp){
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("User not found with email "+request.email()));
        verifyForgotPasswordAttempts(clientIp);
        String token = UUID.randomUUID().toString();
        
        redisService.save(RESET_TOKEN_KEY + token, user.getEmail(), EMAIL_EXPIRE_TIME_IN_SECONDS);

        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
        try{
            emailService.sendForgotPasswordEmail(user.getEmail(), resetLink);
            incrementForgotPasswordAttempts(user.getEmail(), clientIp);
        } catch (Exception err) {
            throw new SMTPServerException("SMTP Server Fail");
        }
        
    }
    
    public void resetPassword(String token, String newPassword, String clientIp) {
        String email = (String) redisService.find(RESET_TOKEN_KEY + token);
        if(email == null){
            throw new IncorrectTokenException("The reset password token is malformed, invalid or expired.");
        }
        User user = userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("User dont find"));
        user.setPassword(securityConfiguration.passwordEncoder().encode(newPassword));
        userRepository.save(user);
        clearForgotPasswordAttempts(email, clientIp);
        redisService.delete(RESET_TOKEN_KEY+token);
    }

    public void isUserTheAuthenticated(User user){
        if (!user.getEmail().equals(getAuthenticatedEmail())) {
            throw new AuthorizationDeniedException("Authenticated User doesn't have permission to perform this action.");
        }
    }

    public String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated())
            return authentication.getName();
        return null;
    }

    public Long getAuthenticatedUserId(){
        String email = getAuthenticatedEmail();
        User user = userRepository.findByEmail(email)
            .orElseThrow(()-> new EntityNotFoundException("User email "+email+" not found"));
        return user.getId();
    }

    private void verifyLoginAttempts(String clientIp){
        String key = BLOCKED_LOGIN_IP_KEY + clientIp;
        boolean ipAddressIsBlocked = redisService.exists(key);
        if (ipAddressIsBlocked)
            throw new MaxRetryException("Ip Address " + clientIp + " login max retry.");
    }

    private void verifyForgotPasswordAttempts(String clientIp){
        String key = BLOCKED_FORGOT_PASSWORD_IP_KEY + clientIp;
        boolean ipAddressIsBlocked = redisService.exists(key);
        if (ipAddressIsBlocked)
            throw new MaxRetryException("Ip Address " + clientIp + " forgot password max retry.");
    }

    private void clearLoginAttempts(String email, String clientIp){
        String key = LOGIN_ATTEMPTS_KEY + ":" + clientIp + ":" + email;
        redisService.delete(key);
    }
    private void clearForgotPasswordAttempts(String email, String clientIp){
        String key = FORGOT_PASSWORD_ATTEMPTS_KEY + ":" + clientIp + ":" + email;
        redisService.delete(key);
    }

    private void incrementLoginAttempts(String email, String clientIp){
        String key = LOGIN_ATTEMPTS_KEY + ":" + clientIp + ":" + email;
        redisService.saveIfDontExists(key, 0, BLOCK_TIME_IN_SECONDS);
        Long attempts = redisService.increment(key);
        
        if (attempts >= MAX_ATTEMPTS) 
            redisService.save(BLOCKED_LOGIN_IP_KEY + clientIp, 1, BLOCK_TIME_IN_SECONDS);
    }
    private void incrementForgotPasswordAttempts(String email, String clientIp){
        String key = FORGOT_PASSWORD_ATTEMPTS_KEY + ":" + clientIp + ":" + email;
        redisService.saveIfDontExists(key, 0, BLOCK_TIME_IN_SECONDS);
        Long attempts = redisService.increment(key);
        
        if (attempts >= MAX_ATTEMPTS) 
            redisService.save(BLOCKED_FORGOT_PASSWORD_IP_KEY + clientIp, 1, BLOCK_TIME_IN_SECONDS);
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