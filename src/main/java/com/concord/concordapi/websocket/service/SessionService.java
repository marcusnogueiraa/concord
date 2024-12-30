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

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void saveSession(String username, WebSocketSession session) {
        sessions.put(username, session);
    }

    public void removeSession(String username) {
        sessions.remove(username);
    }

    public WebSocketSession getSession(String username) {
        return sessions.get(username);
    }

    public void sendMessageToUser(String username, ClientMessage clientMessage) throws Exception {
        WebSocketSession session = sessions.get(username);
        if (session != null && session.isOpen()) {
            System.out.println("aki é pra enviar uma mensagem");
            String jsonMessage = objectMapper.writeValueAsString(clientMessage);
            session.sendMessage(new TextMessage(jsonMessage));
        } else {
            // TODO: Search for node in the cluster responsible for managing the User's WebSocketSession
            System.out.println("Sessão não encontrada ou já fechada para o usuário: " + username);
        }
    }
}