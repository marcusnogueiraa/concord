package com.concord.concordapi.user.dto.response;

import java.time.LocalDateTime;

public record UserDto (
    Long id,
    String name,
    String username,
    String imagePath,
    String email,
    LocalDateTime createdAt
) {}
