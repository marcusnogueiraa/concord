package com.concord.concordapi.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.fileStorage.entity.FilePrefix;
import com.concord.concordapi.fileStorage.service.FileStorageService;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.dto.request.UserPutDto;
import com.concord.concordapi.user.dto.response.UserDto;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.mapper.UserMapper;
import com.concord.concordapi.user.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private FileStorageService fileStorageService;

    public UserDto getById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User "+id+" not found."));
        return UserMapper.toDto(user);
    }

    public UserDto update(UserPutDto userPutDto, Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("User authenticated not found"));
        authService.isUserTheAuthenticated(user);
        if(userPutDto.password() != null) user.setPassword(securityConfiguration.passwordEncoder().encode(userPutDto.password()));
        if(userPutDto.name() != null) user.setName(userPutDto.name());
        if(userPutDto.imageTempPath() != null) {
            FilePrefix prefix = new FilePrefix("user_image");
            fileStorageService.persistImage(prefix ,userPutDto.imageTempPath());
            if(fileStorageService.fileExists(user.getImagePath())){
                fileStorageService.deleteFile(user.getImagePath());
            }
            user.setImagePath(prefix.getDisplayName()+"/"+userPutDto.imageTempPath());
        }
        userRepository.save(user);
        return UserMapper.toDto(user);
    }
}
