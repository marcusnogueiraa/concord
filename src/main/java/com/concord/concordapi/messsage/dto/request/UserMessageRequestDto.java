package com.concord.concordapi.messsage.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserMessageRequestDto (
    @NotNull Long fromUserId
) {}
