package com.concord.concordapi.server.e2e.responses;

import java.util.List;

import com.concord.concordapi.channel.dto.ChannelDTO;

public record ServerExpectedDTO (
    Long id,
    String name,
    UserExpectedDTO owner,
    List<ChannelDTO> channels
){}
