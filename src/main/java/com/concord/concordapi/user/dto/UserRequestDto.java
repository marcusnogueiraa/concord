package com.concord.concordapi.user.dto;

import java.time.LocalDateTime;

public record UserRequestDto (
    Long id,
    String name,
    String username,
    String email,
    LocalDateTime createdAt
) {}
