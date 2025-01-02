package com.concord.concordapi.user.mapper;

import com.concord.concordapi.user.dto.UserRequestDto;
import com.concord.concordapi.user.entity.User;

public class UserMapper {
    public static UserRequestDto toDto(User entity){
        return new UserRequestDto(
            entity.getId(),
            entity.getName(),
            entity.getUsername(),
            entity.getImagePath(),
            entity.getEmail(),
            entity.getCreatedAt()
        );
    }
}
