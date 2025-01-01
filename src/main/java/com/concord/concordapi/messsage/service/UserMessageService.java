package com.concord.concordapi.messsage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.messsage.entity.UserMessage;
import com.concord.concordapi.messsage.repository.UserMessageRepository;
import com.concord.concordapi.websocket.entity.content.UserMessageContent;

@Service
public class UserMessageService {
    
    @Autowired
    private UserMessageRepository userMessageService;

    public void saveUserMessageContent(UserMessageContent userMessageContent, Boolean messageIsRead){
        UserMessage message = UserMessage.builder()
                .fromUserId(userMessageContent.getFrom())
                .toUserId(userMessageContent.getTo())
                .timestamp(userMessageContent.getTimestamp())
                .content(userMessageContent.getMessage())
                .isRead(messageIsRead)
                .build();
        userMessageService.save(message);
    }
}
