package com.concord.concordapi.websocket.listener;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import com.concord.concordapi.websocket.entity.UserEvent;
import com.concord.concordapi.websocket.service.RedisService;
import com.concord.concordapi.websocket.service.SessionService;

@Component
public class RedisMessageListener implements MessageListener {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RedisService redisService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // UserEvent event = (UserEvent) deserialize(message.getBody());
        String channel = new String(message.getChannel());
        channel = channel.split(":")[1];
        String body = new String(message.getBody());
        body = body.split(":")[1];
        Set<String> users = redisService.getUsersSubscribedToServer(Long.parseLong(channel));
        for(String user : users){
            try {
                sessionService.sendMessageToUser(user,body);
            } catch (Exception e) {
                e.printStackTrace();
            }
           
        }
        // // Lógica para processar o evento
        // switch (event.getEventType()) {
        //     case "USER_CONNECTED":
        //         handleUserConnected(event);
        //         break;
        //     case "USER_DISCONNECTED":
        //         handleUserDisconnected(event);
        //         break;
        //     default:
        //         System.out.println("Unknown event type: " + event.getEventType());
        // }
    }
    private void handleUserConnected(UserEvent event) {
        System.out.println("usuario conectado");
    }
    private void handleUserDisconnected(UserEvent event) {
        System.out.println("usuario desconectado");
    }
    private Object deserialize(byte[] data) {
        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(data))) {
            return ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}