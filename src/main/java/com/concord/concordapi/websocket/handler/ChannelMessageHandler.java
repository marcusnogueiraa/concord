package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.websocket.entity.content.ChannelMessageContent;
import com.concord.concordapi.websocket.service.SessionService;

@Component
public class ChannelMessageHandler {
    @Autowired
    private SessionService sessionService; 

    protected void handle(ChannelMessageContent content, WebSocketSession session) {
        try {
            //sendMessage(content, session);
            //persistMessage(content);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem para o usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
