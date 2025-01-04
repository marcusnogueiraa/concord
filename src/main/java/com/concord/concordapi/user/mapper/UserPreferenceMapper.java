package com.concord.concordapi.user.mapper;

import java.util.ArrayList;
import java.util.List;

import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.channel.entity.Channel;
import com.concord.concordapi.channel.mapper.ChannelMapper;
import com.concord.concordapi.user.dto.response.UserDto;
import com.concord.concordapi.user.dto.response.UserPreferenceDto;
import com.concord.concordapi.user.entity.UserPreference;

public class UserPreferenceMapper {
    public static UserPreferenceDto toDto(UserPreference entity){
        UserDto user = UserMapper.toDto(entity.getUser());
        return new UserPreferenceDto(
            user,
            entity.getPreferenceKey(),
            entity.getPreferenceValue()
        );
    }

    public static List<UserPreferenceDto> toDtos(List<UserPreference> entities){
        List<UserPreferenceDto> preferences = new ArrayList<>();
        if(entities!=null){
            for(UserPreference preference : entities){
                preferences.add(toDto(preference));
            }
        }
        return preferences;
    }
}
