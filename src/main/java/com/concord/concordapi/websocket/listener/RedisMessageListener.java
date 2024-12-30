package com.concord.concordapi.websocket.listener;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.concord.concordapi.websocket.entity.ClientMessage;
import com.concord.concordapi.websocket.service.SubscriptionService;
import com.concord.concordapi.websocket.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RedisMessageListener implements MessageListener {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SubscriptionService subscriptionService;

    private final ObjectMapper objectMapper;

    public RedisMessageListener() {
        this.objectMapper = new ObjectMapper();  // Usando Jackson ObjectMapper para deserialização
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        
        String jsonPayload = new String(message.getBody());
        ClientMessage clientMessage;
        try {
            clientMessage = objectMapper.readValue(jsonPayload, ClientMessage.class);
            switch (clientMessage.getType()) {
                case "USER_CONNECTED":
                    handleUserConnected(clientMessage);
                    break;
                case "USER_DISCONNECTED":
                    handleUserDisconnected(clientMessage);
                    break;
                case "TO_SERVER_MESSAGE":
                    handleToServerMessage(clientMessage);
                    break;
                default:
                    System.out.println("Unknown event type: " + clientMessage.getType());
            }
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Lógica para processar o evento
        
    }
    private void handleUserConnected(ClientMessage clientMessage) {
        System.out.println("usuario conectado");
    }
    private void handleUserDisconnected(ClientMessage clientMessage) {
        System.out.println("usuario desconectado");
    }
    private void handleToServerMessage(ClientMessage clientMessage){
        Set<String> users = subscriptionService.getUsersSubscribedToServer(clientMessage.getTo());
        for(String user : users){
            try {
                sessionService.sendMessageToUser(user, clientMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
           
        }
    }

}