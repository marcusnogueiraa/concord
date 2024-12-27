package com.concord.concordapi.shared.dto;


import java.time.LocalDateTime;

public record ErrorResponseDTO (
    String message,
    int status,
    String timestamp,
    String path
) {
    public ErrorResponseDTO(String message, int status, String path) {
        this(message, status, LocalDateTime.now().toString(), path);
    }
} 
