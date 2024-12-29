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
import com.concord.concordapi.websocket.service.SessionService;
import com.concord.concordapi.websocket.service.SubscriptionService;

import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String REDIS_SESSIONS_KEY = "sessions";
    
        private static final String REDIS_CHANNEL = "messages";
    
        @Override
        @Transactional
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            String username = (String) session.getAttributes().get("username");
            if (username != null) {
                sessionService.addSession(username, session);
                User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
                redisTemplate.opsForHash().put(REDIS_SESSIONS_KEY, username, username);
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
            String formattedMessage = sender+":"+payload;
            redisTemplate.convertAndSend(REDIS_CHANNEL, formattedMessage);
        }
    
        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            String username = (String) session.getAttributes().get("username");
            if (username != null) {
                sessionService.removeSession(username);
                // Remove o usuário do mapa ao desconectar
                redisTemplate.opsForHash().delete(REDIS_SESSIONS_KEY, username);
                redisTemplate.convertAndSend(REDIS_CHANNEL, username + " has left.");
            }
        }
    
        // public void sendMessageToUser(String username, String message) throws Exception {
        // String storedUsername = (String) redisTemplate.opsForHash().get(REDIS_SESSIONS_KEY, username);

        //     if (storedUsername != null) {
        //         WebSocketSession session = sessions.get(storedUsername);

        //         if (session != null && session.isOpen()) {
        //             session.sendMessage(new TextMessage(message));
        //         } else {
        //             System.out.println("Sessão local não encontrada ou já fechada para o usuário: " + storedUsername);
        //         }
        //     } else {
        //         System.out.println("Usuário não encontrado no Redis: " + username);
        //     }
        // }
    
}