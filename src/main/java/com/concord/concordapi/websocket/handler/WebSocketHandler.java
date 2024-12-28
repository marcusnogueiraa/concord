package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Usando um mapa para armazenar as sessões dos usuários
    private static final Map<String, WebSocketSession> usersSessions = new ConcurrentHashMap<>();
    private static final String REDIS_CHANNEL = "direct_messages";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            // Armazena a sessão do usuário no mapa
            usersSessions.put(username, session);
            redisTemplate.convertAndSend(REDIS_CHANNEL, username + " has joined.");
            session.sendMessage(new TextMessage("Hello " + username + ", you are connected"));
        } else {
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String sender = (String) session.getAttributes().get("username");
        String[] parts = payload.split(":", 2);

        if (parts.length == 2) {
            String recipient = parts[0].trim();
            String content = parts[1].trim();

            // Formata a mensagem para enviar
            String formattedMessage = sender + " to " + recipient + ": " + content;

            // Envia a mensagem para o Redis
            redisTemplate.convertAndSend(REDIS_CHANNEL, formattedMessage);
        } else {
            session.sendMessage(new TextMessage("Invalid message format. Use 'recipient:message'"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            // Remove o usuário do mapa ao desconectar
            usersSessions.remove(username);
            redisTemplate.convertAndSend(REDIS_CHANNEL, username + " has left.");
        }
    }

    // Método para enviar mensagens para o usuário via WebSocket
    public static void sendMessageToUser(String username, String message) throws Exception {
        WebSocketSession session = usersSessions.get(username);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
}