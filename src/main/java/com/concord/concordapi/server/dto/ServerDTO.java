package com.concord.concordapi.server.dto;

import java.util.List;

import com.concord.concordapi.channel.dto.ChannelDTO;
import com.concord.concordapi.user.dto.UserRequestDto;

public record ServerDTO (
    Long id,
    String name,
    UserRequestDto owner,
    List<ChannelDTO> channels
){}