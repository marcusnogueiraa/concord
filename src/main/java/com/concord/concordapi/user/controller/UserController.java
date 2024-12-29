package com.concord.concordapi.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.user.dto.UserRequestDto;
import com.concord.concordapi.user.service.UserService;

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
    

}
