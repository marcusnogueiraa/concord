package com.concord.concordapi.shared.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.concord.concordapi.shared.dto.ErrorResponseDTO;
import com.concord.concordapi.shared.exception.EntityNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        String message = ex.getMessage();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            message, 
            HttpStatus.NOT_FOUND.value(), 
            request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(errorResponse);
    } 

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = ex.getMessage();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            message, 
            HttpStatus.BAD_REQUEST.value(), 
            request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(errorResponse);
    } 

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder message = new StringBuilder("Validation failed: ");
        
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            message.append(fieldError.getField())
                        .append(" - ")
                        .append(fieldError.getDefaultMessage())
                        .append(", ");
        }


        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            message.toString(), 
            HttpStatus.BAD_REQUEST.value(), 
            request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(errorResponse);
    } 

   
}
