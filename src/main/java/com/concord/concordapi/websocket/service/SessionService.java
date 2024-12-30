package com.concord.concordapi.websocket.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.websocket.entity.ClientMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void saveSession(WebSocketSession session) {
        long userId = getUserIdBySession(session);
        sessions.put(userId, session);
    }

    public void removeSession(WebSocketSession session) {
        long userId = getUserIdBySession(session);
        sessions.remove(userId);
    }

    public WebSocketSession getSession(Long userId) {
        return sessions.get(userId);
    }

    public Long getUserIdBySession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }

    public void sendMessageToUser(Long userId, ClientMessage<?> clientMessage) throws Exception {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            String jsonMessage = objectMapper.writeValueAsString(clientMessage);
            session.sendMessage(new TextMessage(jsonMessage));
        } else {
            System.out.println("Sessão não encontrada ou já fechada para o usuário: " + userId);
        }
    }
}