package com.concord.concordapi.user.dto;


public record UserPreferenceRequestDto (
    String username,
    String preferenceKey,
    String preferenceValue
) {}
