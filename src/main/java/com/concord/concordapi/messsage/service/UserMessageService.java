package com.concord.concordapi.messsage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.concord.concordapi.messsage.dto.response.UserChatSummaryDto;
import com.concord.concordapi.messsage.dto.response.UserMessageResponseDto;
import com.concord.concordapi.messsage.entity.UserChatSummary;
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

    public List<UserChatSummaryDto> getUnreadChatSummaries(Long toUserId) {
        List<UserChatSummary> summaries = userMessageRepository.findUnreadChatSummaries(toUserId);
        return summaries.stream().map(summary ->
            new UserChatSummaryDto(
                summary._id(),
                summary.latestMessageTimestamp(),
                summary.unreadMessagesCount()
            )
        ).collect(Collectors.toList());
    }

    public Page<UserMessageResponseDto> getChatMessages(Long toUserId, Long fromUserId, Pageable pageable) {
        Page<UserMessage> messagesPage = userMessageRepository.findByToUserIdAndFromUserIdOrToUserIdAndFromUserId(
                toUserId, fromUserId, fromUserId, toUserId, pageable
        );

        return messagesPage.map(message -> new UserMessageResponseDto(
                message.getFromUserId(),
                message.getToUserId(),
                message.getMessage(),
                message.getTimestamp()
        ));
    }
}
