package com.concord.concordapi.messsage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.messsage.dto.request.ReadMessagesDto;
import com.concord.concordapi.messsage.entity.UserMessage;
import com.concord.concordapi.messsage.repository.UserMessageRepository;
import com.concord.concordapi.websocket.entity.content.UserMessageContent;

import jakarta.transaction.Transactional;

@Service
public class UserMessageService {
    
    @Autowired
    private UserMessageRepository userMessageRepository;

    public void saveUserMessageContent(UserMessageContent userMessageContent){
        UserMessage message = UserMessage.builder()
                .fromUserId(userMessageContent.getFrom())
                .toUserId(userMessageContent.getTo())
                .timestamp(userMessageContent.getTimestamp())
                .content(userMessageContent.getMessage())
                .isRead(false)
                .build();
        userMessageRepository.save(message);
    }

    @Transactional
    public long markAllMessagesAsRead(ReadMessagesDto readMessages) {
        return userMessageRepository.markMessagesAsRead(readMessages.toUserId(), readMessages.fromUserId());
    }
}
