package com.concord.concordapi.messsage.entity;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "user_messages")
public class UserMessage {
    @Id
    private String id;
    private String fromUserId;      
    private String toUserId;        
    private String content;         
    private boolean isRead;         
    private Instant timestamp;      
}
