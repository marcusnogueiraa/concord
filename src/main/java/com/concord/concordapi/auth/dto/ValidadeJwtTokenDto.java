package com.concord.concordapi.auth.dto;

import jakarta.validation.constraints.NotNull;

public record ValidadeJwtTokenDto(
    @NotNull(message = "Valid token String is required")
    String token
) {}