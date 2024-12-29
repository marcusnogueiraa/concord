package com.concord.concordapi.user.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.exception.IncorrectCodeException;
import com.concord.concordapi.user.exception.UserAlreadyExistsException;
import com.concord.concordapi.auth.entity.UserDetailsImpl;
import com.concord.concordapi.auth.service.JwtTokenService;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.exception.SMTPServerException;
import com.concord.concordapi.shared.service.EmailService;
import com.concord.concordapi.shared.service.RedisService;
import com.concord.concordapi.user.dto.CreateUserDto;
import com.concord.concordapi.user.dto.LoginUserDto;
import com.concord.concordapi.user.dto.RecoveryJwtTokenDto;
import com.concord.concordapi.user.repository.UserRepository;

@Service
public class UserService {
    
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

    private final static String CREATED_USER_CODE_KEY = "created-user-code:";
    private final static int EMAIL_EXPIRE_TIME_IN_SECONDS = 300;

    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserDto.username(), loginUserDto.password());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));
    }

    public void createUser(CreateUserDto createUserDto){
        verifyIfAlreadyExists(createUserDto);
        User newUser = User.builder()
                .name(createUserDto.name())
                .username(createUserDto.username())
                .email(createUserDto.email())
                .password(securityConfiguration.passwordEncoder().encode(
                    createUserDto.password())
                ).build();

        String code = getRandomCode();

        String key = CREATED_USER_CODE_KEY + code;
        redisService.save(key, newUser, EMAIL_EXPIRE_TIME_IN_SECONDS);

        try {
            emailService.sendVerificationEmail(createUserDto.email(), code);
        } catch (Exception err) {
            throw new SMTPServerException("SMTP Server Fail");
        }
    }

    public void confirmUserRegister(String code){
        String key = CREATED_USER_CODE_KEY + code;
        User user = (User) redisService.find(key);
        if (user == null) throw new IncorrectCodeException("Incorrect Code");
        else userRepository.save(user);
    }

    private void verifyIfAlreadyExists(CreateUserDto user){
        if (userRepository.existsByEmail(user.email())) 
            throw new UserAlreadyExistsException("Email " + user.email() + " already exists.");
        if (userRepository.existsByUsername(user.username())) 
            throw new UserAlreadyExistsException("Username " + user.username() + " already exists.");
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