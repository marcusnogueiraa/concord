package com.concord.concordapi.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.websocket.entity.ClientMessage;
import com.concord.concordapi.websocket.entity.content.FriendRequestContent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotificationService {
    
    @Autowired    
    private SessionService sessionService;

    @Autowired    
    private ObjectMapper objectMapper = new ObjectMapper();

    public Boolean sendFriendRequestToUser(Long userId, FriendshipDto friendshipDto)  {
        WebSocketSession session = sessionService.getSession(userId);
        if (session != null && session.isOpen()) {
            ClientMessage<FriendRequestContent> friendshipMessage = new ClientMessage<>()
            String jsonMessage = objectMapper.writeValueAsString(friendshipDto)
        } else {
            
        }
    }

    public Boolean sendMessageToUser(Long userId, ClientMessage<?> messageContent) throws Exception {
        WebSocketSession session = sessionService.getSession(userId);
        if (session != null && session.isOpen()) {
            String jsonMessage = objectMapper.writeValueAsString(messageContent);
            session.sendMessage(new TextMessage(jsonMessage));
            return true;
        } else {
            System.out.println("Sessão não encontrada ou já fechada para o usuário: " + userId);
            return false;
        }
    }
}
