package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.websocket.entity.content.UserMessageContent;
import com.concord.concordapi.websocket.service.SessionService;

@Component
public class UserMessageHandler {

    @Autowired
    private SessionService sessionService; 
    
    protected void handle(UserMessageContent content, WebSocketSession session){
        String recipientUsername = content.getTo();
        WebSocketSession recipientSession = sessionService.getSession(recipientId);
    }

    private void persistMessage(){
        System.out.println("Simulate Persist Message");
    }
}
