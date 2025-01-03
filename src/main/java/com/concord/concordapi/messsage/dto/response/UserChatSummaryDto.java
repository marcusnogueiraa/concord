package com.concord.concordapi.messsage.dto.response;

public record UserChatSummaryDto (
    Long fromUserId,
    Long latestMessageTimestamp,
    Long unreadMessagesCount
) {}
