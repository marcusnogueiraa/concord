package com.concord.concordapi.auth.dto;

public record LoginUserDto (
    String email,
    String password
) {}
