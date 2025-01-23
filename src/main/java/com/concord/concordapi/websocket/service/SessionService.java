package com.concord.concordapi.websocket.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void saveSession(WebSocketSession session) {
        Long userId = getUserIdBySession(session);
        sessions.put(userId, session);
    }

    public void removeSession(WebSocketSession session) {
        Long userId = getUserIdBySession(session);
        sessions.remove(userId);
    }

    public boolean isSaved(WebSocketSession session){
        Long userId = getUserIdBySession(session);
        return userId != null; 
    }

    public WebSocketSession getSession(Long userId) {
        return sessions.get(userId);
    }

    public Long getUserIdBySession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }

}