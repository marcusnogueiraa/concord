package com.concord.concordapi.friendship.dto.response;

import com.concord.concordapi.friendship.entity.FriendshipStatus;
import com.concord.concordapi.user.dto.UserRequestDto;

public record FriendshipDto (
    Long id,
    UserRequestDto from,
    UserRequestDto to,
    FriendshipStatus status
){}
