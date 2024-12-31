package com.concord.concordapi.friendship.dto;

import com.concord.concordapi.user.dto.UserRequestDto;

public record FriendshipDTO (
    Long id,
    UserRequestDto from,
    UserRequestDto to,
    String status
){}
