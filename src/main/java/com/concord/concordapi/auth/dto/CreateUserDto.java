package com.concord.concordapi.auth.dto;

public record CreateUserDto (
    String name,
    String username,
    String email,
    String password
) {}
