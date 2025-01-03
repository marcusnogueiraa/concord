package com.concord.concordapi.auth.dto;

import com.concord.concordapi.user.dto.response.UserDto;

public record RecoveryJwtTokenDto (
    String token,
    UserDto user
) {}
