package com.concord.concordapi.server.dto.response;

import java.util.List;

import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.user.dto.response.UserDto;

public record ServerDto (
    Long id,
    String name,
    String imagePath,
    UserDto owner,
    List<ChannelDto> channels
){}