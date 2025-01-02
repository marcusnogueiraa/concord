package com.concord.concordapi.server.e2e.responses;


public record UserExpectedDTO (
    String name,
    String username,
    String email,
    String createdAt
) {}
