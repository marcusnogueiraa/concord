package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.concord.concordapi.websocket.service.SubscriptionService;

import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    
        private static final String REDIS_CHANNEL = "messages";
    
        @Override
        @Transactional
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            String username = (String) session.getAttributes().get("username");
            if (username != null) {
                User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
                redisTemplate.opsForValue().set("session:" + username, session.getId(), 5, TimeUnit.MINUTES);
                Set<Server> serversSubscribes = user.getServers();
                
    
                for (Server server : serversSubscribes) {
                    subscriptionService.subscribeUserToServer(username, server.getId());
                    session.sendMessage(new TextMessage("Hello " + username + ", you are connected to server " + server.getName()));
                }
            }else {
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
                redisTemplate.delete("session:" + username);
                redisTemplate.convertAndSend(REDIS_CHANNEL, username + " has left.");
            }
        }
    
        // Método para enviar mensagens para o usuário via WebSocket
        public void sendMessageToUser(String username, String message) throws Exception {
            String sessionId = redisTemplate.opsForValue().get("session:" + username);
            if (sessionId != null) {
                WebSocketSession session = getSessionFromRedis(sessionId);
                if (session != null && session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
    }
    private static WebSocketSession getSessionFromRedis(String sessionId) {
        // Esta é uma parte crítica. Como você armazenou o sessionId no Redis, 
        // você precisaria de uma maneira de mapear isso de volta para uma sessão WebSocket 
        // ativa (poderia ser via uma instância centralizada ou com algum mecanismo de 
        // controle de sessão em múltiplos servidores WebSocket).
        return null;
    }

    
}