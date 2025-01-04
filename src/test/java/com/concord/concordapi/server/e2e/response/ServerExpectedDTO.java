package com.concord.concordapi.server.e2e.response;

import java.util.List;

import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.channel.e2e.response.UserExpectedDTO;

public record ServerExpectedDTO (
    Long id,
    String name,
    UserExpectedDTO owner,
    String imagePath,
    List<ChannelDto> channels
){}
