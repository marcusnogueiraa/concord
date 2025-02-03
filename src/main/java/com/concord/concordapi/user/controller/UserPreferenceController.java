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

import com.concord.concordapi.user.dto.request.UserPreferenceRequestDto;
import com.concord.concordapi.user.dto.response.UserPreferenceDto;
import com.concord.concordapi.user.entity.UserPreference;
import com.concord.concordapi.user.service.UserPreferenceService;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/")
public class UserPreferenceController {
    
    @Autowired
    public UserPreferenceService userPreferenceService;

    @GetMapping("/{id}/preferences/{preferenceKey}")
    public ResponseEntity<UserPreferenceDto> getByUsernameAndKey(@PathVariable("id") Long id, @PathVariable("preferenceKey") String preferenceKey){
        UserPreferenceRequestDto request = new UserPreferenceRequestDto(id, preferenceKey, null);
        UserPreferenceDto userPreference = userPreferenceService.get(request);
        return ResponseEntity.ok(userPreference);
    }

    @PostMapping("/{id}/preferences")
    public ResponseEntity<UserPreferenceDto> create(@RequestBody @Valid UserPreferenceRequestDto userPreferenceRequestDto) {
        UserPreferenceDto userPreference = userPreferenceService.create(userPreferenceRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userPreference); 
    }
    @GetMapping("/{id}/preferences")
    public ResponseEntity<List<UserPreferenceDto>> create(@PathVariable("id") Long id) {
        List<UserPreferenceDto> userPreferences = userPreferenceService.getByUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(userPreferences); 
    }
    @DeleteMapping("/{id}/preferences/{preferenceKey}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id, @PathVariable("preferenceKey") String preferenceKey) {
        UserPreferenceRequestDto request = new UserPreferenceRequestDto(id, preferenceKey, null);
        userPreferenceService.delete(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); 
    }

    @PutMapping("/{id}/preferences/{preferenceKey}")
    public ResponseEntity<UserPreferenceDto> update(
        @PathVariable("id") Long id, 
        @PathVariable("preferenceKey") String preferenceKey,
        @RequestBody UserPreferenceRequestDto userPreferenceRequestDto
        ) {
        UserPreferenceRequestDto request = new UserPreferenceRequestDto(id, preferenceKey, userPreferenceRequestDto.preferenceValue());
        UserPreferenceDto userPreference = userPreferenceService.updateValue(request);
        return ResponseEntity.status(HttpStatus.OK).body(userPreference); 
    }

}
