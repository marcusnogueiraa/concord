package com.concord.concordapi.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.user.dto.UserPreferenceRequestDto;
import com.concord.concordapi.user.entity.UserPreference;
import com.concord.concordapi.user.service.UserPreferenceService;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/")
public class UserPreferenceController {
    
    @Autowired
    public UserPreferenceService userPreferenceService;

    @GetMapping("/{username}/preferences/{preferenceKey}")
    public ResponseEntity<UserPreference> getByUsernameAndKey(@PathVariable("username") String username, @PathVariable("preferenceKey") String preferenceKey){
        UserPreferenceRequestDto request = new UserPreferenceRequestDto(username, preferenceKey, null);
        UserPreference userPreference = userPreferenceService.get(request);
        return ResponseEntity.ok(userPreference);
    }

    @PostMapping("/{username}/preferences")
    public ResponseEntity<UserPreference> create(@RequestBody @Valid UserPreferenceRequestDto userPreferenceRequestDto) {
        UserPreference userPreference = userPreferenceService.create(userPreferenceRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userPreference); 
    }
    @GetMapping("/{username}/preferences")
    public ResponseEntity<List<UserPreference>> create(@PathVariable("username") String username) {
        List<UserPreference> userPreferences = userPreferenceService.getByUser(username);
        return ResponseEntity.status(HttpStatus.OK).body(userPreferences); 
    }
    @DeleteMapping("/{username}/preferences/{preferenceKey}")
    public ResponseEntity<?> delete(@PathVariable("username") String username, @PathVariable("preferenceKey") String preferenceKey) {
        UserPreferenceRequestDto request = new UserPreferenceRequestDto(username, preferenceKey, null);
        userPreferenceService.delete(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); 
    }

    @PutMapping("/{username}/preferences/{preferenceKey}")
    public ResponseEntity<UserPreference> update(
        @PathVariable("username") String username, 
        @PathVariable("preferenceKey") String preferenceKey,
        @RequestBody UserPreferenceRequestDto userPreferenceRequestDto
        ) {
        UserPreferenceRequestDto request = new UserPreferenceRequestDto(username, preferenceKey, userPreferenceRequestDto.preferenceValue());
        UserPreference userPreference = userPreferenceService.updateValue(request);
        return ResponseEntity.status(HttpStatus.OK).body(userPreference); 
    }

}
