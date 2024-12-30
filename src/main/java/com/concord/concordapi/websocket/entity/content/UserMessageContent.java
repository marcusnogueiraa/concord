package com.concord.concordapi.websocket.entity.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageContent {
    private long from; 
    private long to;   
    private String message;
    private Long timestamp;
    private Boolean isRead;
}
