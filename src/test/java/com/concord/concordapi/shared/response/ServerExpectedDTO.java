package com.concord.concordapi.shared.response;

import java.util.List;

import com.concord.concordapi.channel.dto.response.ChannelDto;

public record ServerExpectedDTO (
    Long id,
    String name,
    UserExpectedDTO owner,
    String imagePath,
    List<ChannelDto> channels
){}
