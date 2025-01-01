package com.concord.concordapi.messsage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.messsage.dto.request.UserMessageRequestDto;
import com.concord.concordapi.messsage.dto.response.UserMessageResponseDto;
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
                .fromUserId(userMessageContent.getFromUserId())
                .toUserId(userMessageContent.getToUserId())
                .timestamp(userMessageContent.getTimestamp())
                .message(userMessageContent.getMessage())
                .isRead(false)
                .build();
        userMessageRepository.save(message);
    }

    @Transactional
    public void markAllMessagesAsRead(Long toUserId, Long fromUserId) {
        userMessageRepository.markMessagesAsRead(toUserId, fromUserId);
    }

    public List<UserMessageResponseDto> getUnreadMessages(Long toUserId, Long fromUserId) {
        List<UserMessage> messages = userMessageRepository.findUnreadMessagesOrdered(toUserId, fromUserId);
        return messages.stream()
                .map(message -> new UserMessageResponseDto(
                        message.getFromUserId(),
                        message.getToUserId(),
                        message.getMessage(),
                        message.getTimestamp()))
                .collect(Collectors.toList());
    }
}
