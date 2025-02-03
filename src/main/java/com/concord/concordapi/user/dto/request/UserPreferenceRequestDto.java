package com.concord.concordapi.user.dto.request;


public record UserPreferenceRequestDto (
    Long userId,
    String preferenceKey,
    String preferenceValue
) {}
