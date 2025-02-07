package com.concord.concordapi.messsage.dto.response;

import com.concord.concordapi.messsage.entity.message.Message;

public record UserMessageResponseDto (
    Long fromUserId,      
    Long toUserId,       
    Message message,       
    Long timestamp 
) {}
