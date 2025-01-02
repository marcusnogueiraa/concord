package com.concord.concordapi.shared.response;

import java.util.List;

import com.concord.concordapi.channel.dto.ChannelDTO;

public record ServerExpectedDTO (
    Long id,
    String name,
    UserExpectedDTO owner,
    String imagePath,
    List<ChannelDTO> channels
){}
