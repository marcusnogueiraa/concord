package com.concord.concordapi.shared.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.concord.concordapi.shared.dto.ErrorResponseDTO;
import com.concord.concordapi.user.exception.UserAlreadyExistsException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthorizationDenied(AuthorizationDeniedException exc, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            exc.getMessage(),
            HttpStatus.UNAUTHORIZED.value(), 
            request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(EntityNotFoundException exc, HttpServletRequest request) {
        String message = exc.getMessage();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            message, 
            HttpStatus.NOT_FOUND.value(), 
            request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(errorResponse);
    } 

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResourceFound(NoResourceFoundException exc, HttpServletRequest request) {
        String message = exc.getMessage();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            message, 
            HttpStatus.NOT_FOUND.value(), 
            request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(errorResponse);
    } 

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO>  handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        String message = ex.getMessage();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            message, 
            HttpStatus.CONFLICT.value(), 
            request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception exc, HttpServletRequest request){
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            exc.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}