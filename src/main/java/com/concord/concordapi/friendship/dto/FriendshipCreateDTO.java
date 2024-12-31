package com.concord.concordapi.friendship.dto;

public record FriendshipCreateDTO (
    Long fromId,
    Long toId,
    String status
){}
