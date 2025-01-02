package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.messsage.service.UserMessageService;
import com.concord.concordapi.websocket.entity.ClientMessage;
import com.concord.concordapi.websocket.entity.EventType;
import com.concord.concordapi.websocket.entity.content.UserMessageContent;
import com.concord.concordapi.websocket.service.SessionService;

@Component
public class UserMessageHandler {

    @Autowired
    private SessionService sessionService; 

    @Autowired
    private UserMessageService userMessageService;
    
    protected void handle(UserMessageContent content, WebSocketSession session){
        try {
            sendMessageAndPersist(content, session);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem para o usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendMessageAndPersist(UserMessageContent content, WebSocketSession session) throws Exception {
        Long senderId = sessionService.getUserIdBySession(session);
        Long recipientId = content.getToUserId();

        ClientMessage<UserMessageContent> clientMessage = buildMessage(content, senderId);

        sessionService.sendMessageToUser(senderId, clientMessage); 
        sessionService.sendMessageToUser(recipientId, clientMessage);   
    
        userMessageService.saveUserMessageContent(clientMessage.getContent());
    }

    private ClientMessage<UserMessageContent> buildMessage(UserMessageContent content, Long senderId){
        content.setFromUserId(senderId);
        content.setTimestamp(System.currentTimeMillis());
        return ClientMessage.<UserMessageContent>builder()
                .eventType(EventType.USER_MESSAGE)
                .content(content)
                .build();
    }
}