package com.concord.concordapi.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.exception.UserAlreadyExistsException;
import com.concord.concordapi.auth.entity.UserDetailsImpl;
import com.concord.concordapi.auth.service.JwtTokenService;
import com.concord.concordapi.shared.config.SecurityConfiguration;
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

        Integer code = getRandomCode();
    }

    public void persistUser(CreateUserDto createUserDto){
        
        userRepository.save(newUser);
    }

    private void verifyIfAlreadyExists(CreateUserDto user){
        if (userRepository.existsByEmail(user.email())) 
            throw new UserAlreadyExistsException("Email " + user.email() + " already exists.");
        if (userRepository.existsByUsername(user.username())) 
            throw new UserAlreadyExistsException("Username " + user.username() + " already exists.");
    }

    private Integer getRandomCode(){
        
    }

}