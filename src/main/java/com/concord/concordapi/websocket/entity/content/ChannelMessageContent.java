package com.concord.concordapi.websocket.entity.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMessageContent {
    private long from; 
    private long server;
    private long channel;   
    private String message;
    private Long timestamp;
}
