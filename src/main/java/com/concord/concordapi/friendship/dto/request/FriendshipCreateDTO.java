package com.concord.concordapi.friendship.dto.request;

public record FriendshipCreateDTO (
    Long fromId,
    Long toId,
    String status
){}