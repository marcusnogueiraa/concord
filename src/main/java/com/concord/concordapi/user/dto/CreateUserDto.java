package com.concord.concordapi.user.dto;

public record CreateUserDto (
    String name,
    String username,
    String email,
    String password
) {}
