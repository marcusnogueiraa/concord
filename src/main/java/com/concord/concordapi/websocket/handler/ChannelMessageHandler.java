package com.concord.concordapi.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.websocket.entity.content.ChannelMessageContent;

@Component
public class ChannelMessageHandler extends EventHandler<ChannelMessageContent> {

    @Override
    protected void handle(ChannelMessageContent content, WebSocketSession session) {
        if (!sessionService.isSaved(session)) throw new IllegalArgumentException("No WebSocket Authenticaion.");

        try {
            //sendMessage(content, session);
            //persistMessage(content);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem para o usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
