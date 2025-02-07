package com.concord.concordapi.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.fileStorage.entity.FilePrefix;
import com.concord.concordapi.fileStorage.service.FileStorageService;
import com.concord.concordapi.server.dto.response.ServerSummaryDto;
import com.concord.concordapi.server.mapper.ServerMapper;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.dto.request.UserPatchImage;
import com.concord.concordapi.user.dto.request.UserPatchName;
import com.concord.concordapi.user.dto.request.UserPatchUsername;
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
    private FileStorageService fileStorageService;

    public UserDto getById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User "+id+" not found."));
        return UserMapper.toDto(user);
    }

    public UserDto getByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User "+username+" not found."));
        return UserMapper.toDto(user);
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
        .orElseThrow(()->new EntityNotFoundException("User email "+email+" not found"))
        .getId();
    }

    public List<ServerSummaryDto> getServersSummary(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User "+username+" not found."));
        return ServerMapper.toSummaryDtos(user.getServers());
    }

    public UserDto updateUsername(UserPatchUsername userPatchUsername, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("User authenticated not found"));
        authService.isUserTheAuthenticated(user);
        if(userRepository.findByUsername(userPatchUsername.username()).isPresent()){
            throw new RuntimeException("This username is already in use");
        }
        user.setUsername(userPatchUsername.username());
        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    public UserDto updateName(UserPatchName userPatchName, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("User authenticated not found"));
        authService.isUserTheAuthenticated(user);
        user.setName(userPatchName.name());
        userRepository.save(user);
        return UserMapper.toDto(user);
    }
    
    public UserDto updateImage(UserPatchImage userPatchImage, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("User authenticated not found"));
        authService.isUserTheAuthenticated(user);

        FilePrefix prefix = new FilePrefix("user_image");
        fileStorageService.persistFile(prefix ,userPatchImage.imageTempPath());
        if(user.getImagePath() != null && fileStorageService.fileExists(user.getImagePath())){
            fileStorageService.deleteFile(user.getImagePath());
        }
        user.setImagePath(prefix.getDisplayName()+"/"+userPatchImage.imageTempPath());
        userRepository.save(user);
        return UserMapper.toDto(user);
    }
}
