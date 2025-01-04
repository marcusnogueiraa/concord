package com.concord.concordapi.channel.e2e.response;


public record UserExpectedDTO (
    Long id,
    String name,
    String username,
    String email,
    String imagePath,
    String createdAt
) {}
