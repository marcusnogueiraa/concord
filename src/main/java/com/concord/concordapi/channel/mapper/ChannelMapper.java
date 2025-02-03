package com.concord.concordapi.channel.mapper;

import java.util.ArrayList;
import java.util.List;

import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.channel.entity.Channel;

public class ChannelMapper {
    public static ChannelDto toDto(Channel entity){
        return new ChannelDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription()
        );
    }
    
    public static List<ChannelDto> toDtos(List<Channel> entities){
        List<ChannelDto> channelDtos = new ArrayList<>();
        if(entities!=null){
            for(Channel channel : entities){
                channelDtos.add(toDto(channel));
            }
        }
        return channelDtos;
    }
}
