package com.concord.concordapi.friendship.dto.response;

import com.concord.concordapi.friendship.entity.FriendshipStatus;
import com.concord.concordapi.user.dto.response.UserDto;

public record FriendshipDto (
    Long id,
    UserDto from,
    UserDto to,
    FriendshipStatus status
){}
