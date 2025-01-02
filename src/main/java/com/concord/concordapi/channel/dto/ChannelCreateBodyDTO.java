package com.concord.concordapi.channel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChannelCreateBodyDTO(
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    String name,

    @NotNull(message = "Server ID is required")
    Long serverId,

    String description){
}
