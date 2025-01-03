package com.concord.concordapi.user.dto.response;

public record UserPreferenceDto (
    UserDto user,
    String key,
    String value
){}
