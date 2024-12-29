package com.concord.concordapi.user.mapper;

import com.concord.concordapi.user.dto.UserRequestDto;
import com.concord.concordapi.user.entity.User;

public class UserMapper {
    public static UserRequestDto toDto(User entity){
        return new UserRequestDto(
            entity.getName(),
            entity.getUsername(),
            entity.getEmail(),
            entity.getCreatedAt()
        );
    }
}
