package com.concord.concordapi.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.user.dto.ConfirmationCode;
import com.concord.concordapi.user.dto.CreateUserDto;
import com.concord.concordapi.user.dto.LoginUserDto;
import com.concord.concordapi.user.dto.RecoveryJwtTokenDto;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody LoginUserDto loginUserDto, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        RecoveryJwtTokenDto token = authService.authenticateUser(loginUserDto, clientIp);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody CreateUserDto createUserDto) {
        authService.registerUser(createUserDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmUserRegister(@RequestBody ConfirmationCode confirmationCode) {
        authService.confirmUserRegister(confirmationCode.code());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    
}
