package com.concord.concordapi.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.channel.dto.ChannelPutBodyDTO;
import com.concord.concordapi.user.dto.UserRequestDto;
import com.concord.concordapi.user.dto.UserPutDto;
import com.concord.concordapi.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    public UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserRequestDto> getByUsername(@PathVariable("username") String username){
        UserRequestDto user = userService.getByUsername(username);
        return ResponseEntity.ok(user);
    }
    @PutMapping("/{username}")
    public ResponseEntity<UserRequestDto> update(@RequestBody UserPutDto userPutDto){
        UserRequestDto user = userService.update(userPutDto);
        return ResponseEntity.ok(user);
    }
}
