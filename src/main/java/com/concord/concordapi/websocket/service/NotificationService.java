package com.concord.concordapi.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.websocket.entity.ClientMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotificationService {
    
    @Autowired    
    private SessionService sessionService;

    @Autowired    
    private ObjectMapper objectMapper = new ObjectMapper();

    public Boolean sendMessageToUser(Long userId, ClientMessage<?> clientMessage) throws Exception {
        WebSocketSession session = sessionService.getSession(userId);
        if (session != null && session.isOpen()) {
            String jsonMessage = objectMapper.writeValueAsString(clientMessage);
            session.sendMessage(new TextMessage(jsonMessage));
            return true;
        } else {
            System.out.println("Sessão não encontrada ou já fechada para o usuário: " + userId);
            return false;
        }
    }
}
