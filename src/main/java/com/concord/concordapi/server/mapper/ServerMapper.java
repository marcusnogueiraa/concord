package com.concord.concordapi.server.mapper;

import java.util.ArrayList;
import java.util.List;

import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.channel.entity.Channel;
import com.concord.concordapi.channel.mapper.ChannelMapper;
import com.concord.concordapi.server.dto.response.ServerDto;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.user.dto.response.UserDto;
import com.concord.concordapi.user.mapper.UserMapper;

public class ServerMapper {
    public static ServerDto toDto(Server entity){
        UserDto owner = UserMapper.toDto(entity.getOwner());
        List<ChannelDto> channelDtos = ChannelMapper.toDtos(entity.getChannels());
        return new ServerDto(
            entity.getId(),
            entity.getName(),
            entity.getImagePath(),
            owner,
            channelDtos
        );
    }
}
