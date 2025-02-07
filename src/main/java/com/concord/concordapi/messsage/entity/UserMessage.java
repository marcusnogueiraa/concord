package com.concord.concordapi.messsage.entity;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.concord.concordapi.messsage.entity.message.Message;
import com.concord.concordapi.messsage.entity.message.MessageType;

@Data
@Builder
@Document(collection = "user_messages")
public class UserMessage {
    @Id
    private String id;
    private Long fromUserId;      
    private Long toUserId;    
    private boolean isRead;         
    private Long timestamp; 
    private MessageType type;   
    private Message message;              
}
