package com.concord.concordapi.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.auth.dto.ConfirmationCode;
import com.concord.concordapi.auth.dto.CreateUserDto;
import com.concord.concordapi.auth.dto.ForgotPasswordRequest;
import com.concord.concordapi.auth.dto.LoginUserDto;
import com.concord.concordapi.auth.dto.RecoveryJwtTokenDto;
import com.concord.concordapi.auth.dto.ResetPasswordRequest;
import com.concord.concordapi.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        authService.sendForgotPassword(forgotPasswordRequest, clientIp);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String token, @RequestBody @Valid ResetPasswordRequest requestReset, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        authService.resetPassword(token, requestReset.newPassword(), clientIp);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    
}
