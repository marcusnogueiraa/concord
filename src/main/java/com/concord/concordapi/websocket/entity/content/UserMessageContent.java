package com.concord.concordapi.websocket.entity.content;

import com.concord.concordapi.messsage.entity.message.Message;
import com.concord.concordapi.messsage.entity.message.MessageFactory;
import com.concord.concordapi.messsage.entity.message.MessageType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageContent {
    private long fromUserId; 
    private long toUserId; 
    private MessageType type;  
    private Message message;
    private Long timestamp;

    public static UserMessageContent deserialize(JsonNode node){
        MessageType type = MessageType.valueOf(node.get("type").asText());
        return new UserMessageContent(
            node.get("fromUserId").asLong(),
            node.get("toUserId").asLong(),
            type,
            MessageFactory.createMessage(type, node.get("message")),
            node.get("timestamp").asLong()
        );
    }
}
