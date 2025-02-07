package com.concord.concordapi.message.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.concord.concordapi.fileStorage.entity.FilePrefix;
import com.concord.concordapi.fileStorage.service.FileStorageService;
import com.concord.concordapi.messsage.dto.response.UserChatSummaryDto;
import com.concord.concordapi.messsage.dto.response.UserMessageResponseDto;
import com.concord.concordapi.messsage.entity.UserChatSummary;
import com.concord.concordapi.messsage.entity.UserMessage;
import com.concord.concordapi.messsage.entity.message.MessageFile;
import com.concord.concordapi.messsage.entity.message.MessageText;
import com.concord.concordapi.messsage.entity.message.MessageType;
import com.concord.concordapi.messsage.repository.UserMessageRepository;
import com.concord.concordapi.messsage.service.UserMessageService;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.websocket.entity.content.UserMessageContent;

@ExtendWith(MockitoExtension.class)
class UserMessageServiceTest {

    @Mock
    private FileStorageService fileStorageService;
    
    @Mock
    private UserMessageRepository userMessageRepository;

    @InjectMocks
    private UserMessageService userMessageService;

    private User to;
    private User from;
    private UserMessageContent userMessageContent;
    private UserMessage userMessage;

    static Random random;
    
    @BeforeAll
    static void setup(){
        random = new Random();
    }

    @BeforeEach
    void setEach() {
        to = new User(random.nextLong(), "to", "to", "to@email.com", "123", null, new ArrayList(), null, null);
        from = new User(random.nextLong(), "from", "from", "from@email.com", "123", null, new ArrayList(), null, null);
        MessageText messageText = new MessageText("hello");
        userMessageContent = new UserMessageContent(from.getId(), to.getId(), MessageType.TEXT, messageText, 123456789L);
        userMessage = UserMessage.builder()
                .fromUserId(from.getId())
                .toUserId(to.getId())
                .timestamp(123456789L)
                .message(messageText)
                .type(MessageType.TEXT)
                .isRead(false)
                .build();
    }

    @Test
    void shouldSaveUserMessageContent() {
        userMessageService.saveUserMessageContent(userMessageContent);
        verify(userMessageRepository, times(1)).save(any(UserMessage.class));
    }

    @Test
    void shouldSaveUserMessageContentWithFile() {
        MessageFile messageFile = new MessageFile("4124.jpg");
        UserMessageContent fileMessageContent = new UserMessageContent(from.getId(), to.getId(), MessageType.FILE, messageFile, 123456789L);
        userMessageService.saveUserMessageContent(fileMessageContent);

        verify(fileStorageService, times(1)).persistImage(any(FilePrefix.class), eq(messageFile.getPath()));
        verify(userMessageRepository, times(1)).save(any(UserMessage.class));
    }

    @Test
    void shouldMarkAllMessagesAsRead() {
        userMessageService.markAllMessagesAsRead(to.getId(), from.getId());
        verify(userMessageRepository, times(1)).markMessagesAsRead(to.getId(), from.getId());
    }

    @Test
    void shouldGetUnreadChatSummaries() {
        List<UserChatSummary> summaries = List.of(new UserChatSummary(from.getId(), 123456789L, 5L));
        when(userMessageRepository.findUnreadChatSummaries(to.getId())).thenReturn(summaries);

        List<UserChatSummaryDto> result = userMessageService.getUnreadChatSummaries(to.getId());

        assertEquals(1, result.size());
        assertEquals(from.getId(), result.get(0).fromUserId());
        assertEquals(123456789L, result.get(0).latestMessageTimestamp());
        assertEquals(5L, result.get(0).unreadMessagesCount());
    }

    @Test
    void shouldGetChatMessages() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserMessage> messagePage = new PageImpl<>(List.of(userMessage));
        when(userMessageRepository.findByToUserIdAndFromUserIdOrToUserIdAndFromUserId(to.getId(), from.getId(), from.getId(), to.getId(), pageable)).thenReturn(messagePage);

        Page<UserMessageResponseDto> result = userMessageService.getChatMessages(to.getId(), from.getId(), pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(from.getId(), result.getContent().get(0).fromUserId());
        assertEquals("hello", result.getContent().get(0).message().getText());
    }
}
