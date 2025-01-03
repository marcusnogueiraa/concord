package com.concord.concordapi.friendship.mapper;

import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.friendship.entity.Friendship;
import com.concord.concordapi.user.dto.response.UserDto;
import com.concord.concordapi.user.entity.User;

public class FriendshipMapper {
    public static FriendshipDto toDto(Friendship entity){
        UserDto FromRequest = makeUserRequestDTOByUser(entity.getFrom_user());
        UserDto ToRequest =  makeUserRequestDTOByUser(entity.getTo_user());
        return new FriendshipDto(
            entity.getId(),
            FromRequest,
            ToRequest,
            entity.getStatus()
        );
    }
    private static UserDto makeUserRequestDTOByUser(User user){
        return new UserDto(user.getId(),user.getName(), user.getUsername(), user.getImagePath(), user.getEmail(), user.getCreatedAt());
    }
}
