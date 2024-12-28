package com.concord.concordapi.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New connection established: " + session.getId());
        String username = (String) session.getAttributes().get("username");
        session.sendMessage(new TextMessage("Hello "+username+", you are connected"));
    }
    

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message: " + message.getPayload());
        String username = (String) session.getAttributes().get("username");
        session.sendMessage(new TextMessage("Server received from "+username+": " + message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Connection closed: " + session.getId());
    }
}