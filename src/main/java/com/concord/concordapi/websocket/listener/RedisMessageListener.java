package com.concord.concordapi.websocket.listener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import com.concord.concordapi.websocket.service.SessionService;

@Component
public class RedisMessageListener implements MessageListener {

    @Autowired
    private SessionService sessionService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageContent = new String(message.getBody());
            System.out.println("Mensagem recebida do Redis: " + messageContent);

            // Supondo formato "username:mensagem"
            String[] parts = messageContent.split(":", 2);
            if (parts.length == 2) {
                String recipient = parts[0].trim();
                String content = parts[1].trim();
                sessionService.sendMessageToUser(recipient, content);
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem do Redis: " + e.getMessage());
        }
    }
}