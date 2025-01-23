package com.concord.concordapi.websocket.entity.content;

import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.friendship.entity.FriendshipStatus;
import com.concord.concordapi.user.dto.response.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestContent {
    private Long id;
    private UserDto from;
    private UserDto to;
    private FriendshipStatus status;

    public FriendRequestContent(FriendshipDto friendshipDto) {
        this.id = friendshipDto.id();
        this.from = friendshipDto.from();
        this.to = friendshipDto.to();
        this.status = friendshipDto.status();
    }
}
