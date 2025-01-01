package com.concord.concordapi.messsage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.messsage.dto.request.UserMessageRequestDto;
import com.concord.concordapi.messsage.dto.response.UserMessageResponseDto;
import com.concord.concordapi.messsage.service.UserMessageService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/messages")
public class UserMessageController {
    
    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private AuthService authService;

    @GetMapping("/unread")
    public ResponseEntity<List<UserMessageResponseDto>> getUnreadMessages (
        @RequestParam Long toUserId, @RequestParam Long fromUserId){
        
        List<UserMessageResponseDto> unreadMessages = userMessageService.getUnreadMessages(toUserId, fromUserId);
        return ResponseEntity.ok(unreadMessages);
    }

    
    @PatchMapping("/read")
    public ResponseEntity<?> markAllMessagesAsRead(@RequestBody @Valid UserMessageRequestDto userMessageRequest){
        userMessageService.markAllMessagesAsRead(userMessageRequest.toUserId(), userMessageRequest.fromUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chat")
    public ResponseEntity<Page<UserMessageResponseDto>> getChatMessages(
            @RequestParam Long toUserId,
            @RequestParam Long fromUserId,
            Pageable pageable) {
        
        checkIfTheAuthenticatedUserHasAccessToChat(toUserId, fromUserId);
        Page<UserMessageResponseDto> messagesPage = userMessageService.getChatMessages(toUserId, fromUserId, pageable);
        return ResponseEntity.ok(messagesPage);
    }

    private void checkIfTheAuthenticatedUserHasAccessToChat(Long toUserId, Long fromUserId){
        Long authenticatedUserId = authService.getAuthenticatedUserId();
        if (!(authenticatedUserId == toUserId || authenticatedUserId == fromUserId))
            throw new IllegalArgumentException("The user does not have access to this chat.");
    }
}
