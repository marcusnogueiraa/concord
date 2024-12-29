package com.concord.concordapi.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.user.dto.ConfirmationCode;
import com.concord.concordapi.user.dto.CreateUserDto;
import com.concord.concordapi.user.dto.LoginUserDto;
import com.concord.concordapi.user.dto.RecoveryJwtTokenDto;
import com.concord.concordapi.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody LoginUserDto loginUserDto, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        RecoveryJwtTokenDto token = userService.authenticateUser(loginUserDto, clientIp);
        return ResponseEntity.ok(token);
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmUserRegister(@RequestBody ConfirmationCode confirmationCode) {
        userService.confirmUserRegister(confirmationCode.code());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    
}
