package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.websocket.entity.ClientMessage;
import com.concord.concordapi.websocket.entity.EventType;
import com.concord.concordapi.websocket.entity.content.UserMessageContent;
import com.concord.concordapi.websocket.service.SessionService;

@Component
public class UserMessageHandler {

    @Autowired
    private SessionService sessionService; 
    
    protected void handle(UserMessageContent content, WebSocketSession session){
        try {
            Long recipientId = content.getTo();

            ClientMessage<UserMessageContent> clientMessage = ClientMessage.<UserMessageContent>builder()
                    .eventType(EventType.USER_MESSAGE)
                    .content(content)
                    .build();

            sessionService.sendMessageToUser(recipientId, clientMessage);
            persistMessage();
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem para o usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void persistMessage(){
        System.out.println("Simulate Persist Message");
    }
}
