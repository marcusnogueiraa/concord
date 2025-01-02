package com.concord.concordapi.user.dto;

public record UserPutDto (
    String name,
    String password,
    String imageTempPath
) {}
