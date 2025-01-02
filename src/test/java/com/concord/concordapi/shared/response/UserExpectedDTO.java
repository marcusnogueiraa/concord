package com.concord.concordapi.shared.response;


public record UserExpectedDTO (
    Long id,
    String name,
    String username,
    String email,
    String imagePath,
    String createdAt
) {}
