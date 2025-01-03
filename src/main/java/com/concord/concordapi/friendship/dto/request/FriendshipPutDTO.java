package com.concord.concordapi.friendship.dto.request;

import com.concord.concordapi.friendship.entity.FriendshipStatus;

public record FriendshipPutDTO (
    Long id,
    FriendshipStatus status
){}
