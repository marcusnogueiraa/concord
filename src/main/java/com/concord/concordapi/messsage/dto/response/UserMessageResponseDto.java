package com.concord.concordapi.messsage.dto.response;

public record UserMessageResponseDto (
    Long fromUserId,      
    Long toUserId,       
    String content,       
    Long timestamp 
) {}
