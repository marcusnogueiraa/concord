package com.concord.concordapi.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.dto.UserPreferenceRequestDto;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.entity.UserPreference;
import com.concord.concordapi.user.repository.UserPreferenceRepository;
import com.concord.concordapi.user.repository.UserRepository;


@Service
public class UserPreferenceService {
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authInfoService;

    public UserPreference get(UserPreferenceRequestDto userPreferenceRequestDto){
        if (!userPreferenceRequestDto.username().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("User doesn't match the logged-in user");
        }
        UserPreference userPreference = userPreferenceRepository.findByUserUsernameAndPreferenceKey(
            userPreferenceRequestDto.username(), 
            userPreferenceRequestDto.preferenceKey()
            ).orElseThrow(()-> new EntityNotFoundException("Preference not found"));
        return userPreference;
    }
    public List<UserPreference> getByUser(String username){
        if (!username.equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("User doesn't match the logged-in user");
        }
        User user = userRepository.findByUsername(username).orElseThrow(()->
            new EntityNotFoundException("User not found")
        );
        List<UserPreference> userPreferences = userPreferenceRepository.findByUser(user);
        return userPreferences;
        
    }
    public UserPreference create(UserPreferenceRequestDto userPreferenceRequestDto){
        if (!userPreferenceRequestDto.username().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("User doesn't match the logged-in user");
        }
        String username = userPreferenceRequestDto.username();
        User user = userRepository.findByUsername(username).orElseThrow(()->
            new EntityNotFoundException("User not found")
        );
        UserPreference userPreference = new UserPreference();
        userPreference.setUser(user);
        userPreference.setPreferenceKey(userPreferenceRequestDto.preferenceKey());
        userPreference.setPreferenceValue(userPreferenceRequestDto.preferenceValue());
        userPreferenceRepository.save(userPreference);
        return userPreference;
    }
    public void delete(UserPreferenceRequestDto userPreferenceRequestDto){
        if (!userPreferenceRequestDto.username().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("User doesn't match the logged-in user");
        }
        UserPreference userPreference = userPreferenceRepository.findByUserUsernameAndPreferenceKey(
            userPreferenceRequestDto.username(), 
            userPreferenceRequestDto.preferenceKey()
            ).orElseThrow(()-> new EntityNotFoundException("Preference not found"));
        userPreferenceRepository.delete(userPreference);
    }
    public UserPreference updateValue(UserPreferenceRequestDto userPreferenceRequestDto){
        if (!userPreferenceRequestDto.username().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("User doesn't match the logged-in user");
        }
        UserPreference userPreference = userPreferenceRepository.findByUserUsernameAndPreferenceKey(
            userPreferenceRequestDto.username(), 
            userPreferenceRequestDto.preferenceKey()
            ).orElseThrow(()-> new EntityNotFoundException("Preference not found"));
        userPreference.setPreferenceValue(userPreferenceRequestDto.preferenceValue());
        userPreferenceRepository.save(userPreference);
        return userPreference;
    }
    
}
