package com.concord.concordapi.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.fileStorage.entity.FilePrefix;
import com.concord.concordapi.fileStorage.service.FileStorageService;
import com.concord.concordapi.shared.config.SecurityConfiguration;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.dto.UserPutDto;
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
    @Autowired
    private FileStorageService fileStorageService;

    public UserRequestDto getByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User "+username+" not found."));
        return UserMapper.toDto(user);
    }

    public Long findUserId(String username) {
        return userRepository.getIdByUsername(username);
    }
    
    public UserRequestDto update(UserPutDto userPutDto, String username){
        if (!username.equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Username doesn't match the logged-in user");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("User authenticated not found"));
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
