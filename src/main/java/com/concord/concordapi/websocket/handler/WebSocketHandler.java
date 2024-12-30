package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.concord.concordapi.websocket.service.SessionService;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SessionService sessionService;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    } 
}