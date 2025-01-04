package com.concord.concordapi.user.dto.request;

public record UserPutDto (
    String name,
    String password,
    String imageTempPath
) {}
