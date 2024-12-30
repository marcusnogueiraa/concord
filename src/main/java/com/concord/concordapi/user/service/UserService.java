package com.concord.concordapi.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.dto.UserRequestDto;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.mapper.UserMapper;
import com.concord.concordapi.user.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authInfoService;
    @Autowired
    private SecurityConfiguration securityConfiguration;

    public UserRequestDto getByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User "+username+" not found."));
        return UserMapper.toDto(user);
    }
    public UserRequestDto changePassword(String password){
        User user = userRepository.findByUsername(authInfoService.getAuthenticatedUsername())
                .orElseThrow(()-> new EntityNotFoundException("User authenticated not found"));
        user.setPassword(securityConfiguration.passwordEncoder().encode(password));
        userRepository.save(user);
        return UserMapper.toDto(user);
    }
}
