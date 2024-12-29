package com.concord.concordapi.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.user.dto.UserRequestDto;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.exception.UserNotFoundException;
import com.concord.concordapi.user.mapper.UserMapper;
import com.concord.concordapi.user.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public UserRequestDto getByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User "+username+" not found."));
        return UserMapper.toDto(user);
    }
}
