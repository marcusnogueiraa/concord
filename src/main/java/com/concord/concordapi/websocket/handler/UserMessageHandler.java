package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.messsage.entity.message.Message;
import com.concord.concordapi.messsage.entity.message.MessageFactory;
import com.concord.concordapi.messsage.service.UserMessageService;
import com.concord.concordapi.websocket.entity.ClientMessage;
import com.concord.concordapi.websocket.entity.EventType;
import com.concord.concordapi.websocket.entity.content.UserMessageContent;
import com.concord.concordapi.websocket.service.NotificationService;

@Component
public class UserMessageHandler extends EventHandler<UserMessageContent>{

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private NotificationService notificationService;
    
    @Override
    protected void handle(UserMessageContent content, WebSocketSession session){
        if (!sessionService.isSaved(session)) throw new IllegalArgumentException("No WebSocket Authenticaion.");

        try {
            sendMessageAndPersist(content, session);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem para o usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    protected void sendMessageAndPersist(UserMessageContent content, WebSocketSession session) throws Exception {
        Long senderId = sessionService.getUserIdBySession(session);
        Long recipientId = content.getToUserId();
        ClientMessage<UserMessageContent> clientMessage = buildMessage(content, senderId);
        userMessageService.saveUserMessageContent(clientMessage.getContent());
        notificationService.sendMessageToUser(senderId, clientMessage); 
        notificationService.sendMessageToUser(recipientId, clientMessage);   
    
        
    }

    private ClientMessage<UserMessageContent> buildMessage(UserMessageContent content, Long senderId){
        content.setFromUserId(senderId);
        content.setTimestamp(System.currentTimeMillis());
        content.setMessage(MessageFactory.createMessage(content.getType(), content.getMessage()));
        return ClientMessage.<UserMessageContent>builder()
                .eventType(EventType.USER_MESSAGE)
                .content(content)
                .build();
    }
}
