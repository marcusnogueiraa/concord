package com.concord.concordapi.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.dto.request.UserPreferenceRequestDto;
import com.concord.concordapi.user.dto.response.UserPreferenceDto;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.entity.UserPreference;
import com.concord.concordapi.user.mapper.UserPreferenceMapper;
import com.concord.concordapi.user.repository.UserPreferenceRepository;
import com.concord.concordapi.user.repository.UserRepository;


@Service
public class UserPreferenceService {
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    public UserPreferenceDto get(UserPreferenceRequestDto userPreferenceRequestDto){
        User user = userRepository.findById(userPreferenceRequestDto.userId()).orElseThrow(()->new EntityNotFoundException("User id "+userPreferenceRequestDto.userId()+"not found"));
        authService.isUserTheAuthenticated(user);
        UserPreference userPreference = userPreferenceRepository.findByUserUsernameAndPreferenceKey(
            user.getUsername(), 
            userPreferenceRequestDto.preferenceKey()
            ).orElseThrow(()-> new EntityNotFoundException("Preference not found"));
        return UserPreferenceMapper.toDto(userPreference);
    }
    public List<UserPreferenceDto> getByUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("User id "+id+"not found"));
        authService.isUserTheAuthenticated(user);
        List<UserPreference> userPreferences = userPreferenceRepository.findByUser(user);
        return UserPreferenceMapper.toDtos(userPreferences);
        
    }
    public UserPreferenceDto create(UserPreferenceRequestDto userPreferenceRequestDto){
        User user = userRepository.findById(userPreferenceRequestDto.userId()).orElseThrow(()->
            new EntityNotFoundException("User id "+userPreferenceRequestDto.userId()+" not found")
        );  
        authService.isUserTheAuthenticated(user);
        UserPreference userPreference = new UserPreference(
            null, 
            user, 
            userPreferenceRequestDto.preferenceKey(), 
            userPreferenceRequestDto.preferenceValue()
        );
        userPreferenceRepository.save(userPreference);
        return UserPreferenceMapper.toDto(userPreference);
    }
    public void delete(UserPreferenceRequestDto userPreferenceRequestDto){
        User user = userRepository.findById(userPreferenceRequestDto.userId()).orElseThrow(()->
            new EntityNotFoundException("User id "+userPreferenceRequestDto.userId()+" not found")
        );  
        authService.isUserTheAuthenticated(user);
        UserPreference userPreference = userPreferenceRepository.findByUserUsernameAndPreferenceKey(
            user.getUsername(), 
            userPreferenceRequestDto.preferenceKey()
        ).orElseThrow(()-> new EntityNotFoundException("Preference not found"));
        userPreferenceRepository.delete(userPreference);
    }
    public UserPreferenceDto updateValue(UserPreferenceRequestDto userPreferenceRequestDto){
        User user = userRepository.findById(userPreferenceRequestDto.userId()).orElseThrow(()->
            new EntityNotFoundException("User id "+userPreferenceRequestDto.userId()+" not found")
        );  
        authService.isUserTheAuthenticated(user);
        UserPreference userPreference = userPreferenceRepository.findByUserUsernameAndPreferenceKey(
            user.getUsername(), 
            userPreferenceRequestDto.preferenceKey()
            ).orElseThrow(()-> new EntityNotFoundException("Preference not found"));
        userPreference.setPreferenceValue(userPreferenceRequestDto.preferenceValue());
        userPreferenceRepository.save(userPreference);
        return UserPreferenceMapper.toDto(userPreference);
    }
    
}
