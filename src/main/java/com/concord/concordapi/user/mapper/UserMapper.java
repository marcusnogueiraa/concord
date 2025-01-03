package com.concord.concordapi.user.mapper;

import com.concord.concordapi.user.dto.response.UserDto;
import com.concord.concordapi.user.entity.User;

public class UserMapper {
    public static UserDto toDto(User entity){
        return new UserDto(
            entity.getId(),
            entity.getName(),
            entity.getUsername(),
            entity.getImagePath(),
            entity.getEmail(),
            entity.getCreatedAt()
        );
    }
}
