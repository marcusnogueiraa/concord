package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.concord.concordapi.websocket.entity.ClientMessage;
import com.concord.concordapi.websocket.entity.content.ChannelMessageContent;
import com.concord.concordapi.websocket.entity.content.ConnectContent;
import com.concord.concordapi.websocket.entity.content.UserMessageContent;
import com.concord.concordapi.websocket.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ConnectHandler connectHandler;
    @Autowired
    private UserMessageHandler userMessageHandler;
    @Autowired
    private ChannelMessageHandler channelMessageHandler;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New Websocket Connection");
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ClientMessage<?> clientMessage = objectMapper.readValue(message.getPayload(), ClientMessage.class);
        delegateHandler(clientMessage, session);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = sessionService.getUserIdBySession(session);
        System.out.println("User disconnected :" + userId);
        if (userId != null) sessionService.removeSession(session);
    }

    private void delegateHandler(ClientMessage<?> clientMessage, WebSocketSession session) throws Exception {
        switch (clientMessage.getEventType()) {
            case CONNECT -> {
                ConnectContent content = objectMapper.convertValue(clientMessage.getContent(), ConnectContent.class);
                connectHandler.handle(content, session);
            }
            case USER_MESSAGE -> {
                UserMessageContent content = objectMapper.convertValue(clientMessage.getContent(), UserMessageContent.class);
                userMessageHandler.handle(content, session);
            }
            case CHANNEL_MESSAGE -> {
                ChannelMessageContent content = objectMapper.convertValue(clientMessage.getContent(), ChannelMessageContent.class);
                channelMessageHandler.handle(content, session);
            }
            default -> throw new IllegalArgumentException("Unknown EventType: " + clientMessage.getEventType());
        }
    }
}