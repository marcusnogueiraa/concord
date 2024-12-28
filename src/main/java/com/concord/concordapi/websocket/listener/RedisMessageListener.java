package com.concord.concordapi.websocket.listener;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.concord.concordapi.websocket.handler.WebSocketHandler;

@Component
public class RedisMessageListener implements MessageListener {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = message.toString();

        // Aqui você pode processar a mensagem recebida do Redis
        // Exemplo: Enviar para o usuário específico
        // Exemplo simples para enviar a mensagem a todos os usuários conectados
        // Você pode customizar isso para direcionar a mensagem para o destinatário correto
        try {
            // Lógica para enviar a mensagem para o WebSocket
            webSocketHandler.sendMessageToUser("someUser", messageBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}