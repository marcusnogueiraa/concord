package com.concord.concordapi.shared.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;


@Component
public class RedisMessageListener implements MessageListener {

    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = message.toString();

        System.out.println("uma mensagem foi capturada pelo redis listener");
        System.out.println(messageBody);
        try {
            // Lógica para enviar a mensagem para o WebSocket
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}