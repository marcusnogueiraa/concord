package com.concord.concordapi.messsage.entity;

public record UserChatSummary (
    Long _id,
    Long latestMessageTimestamp,
    Long unreadMessagesCount
) {}
