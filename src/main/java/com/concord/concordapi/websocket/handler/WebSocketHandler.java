package com.concord.concordapi.websocket.handler;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.concord.concordapi.websocket.entity.UserEvent;
import com.concord.concordapi.websocket.service.RedisService;
import com.concord.concordapi.websocket.service.SessionService;

import jakarta.transaction.Transactional;


@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private RedisService redisService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;
    
    private static final String REDIS_SESSIONS_KEY = "sessions";
    
        @Override
        @Transactional
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            String username = (String) session.getAttributes().get("username");
            if (username != null) {
                sessionService.addSession(username, session);
                redisService.getRedisTemplate().opsForHash().put(REDIS_SESSIONS_KEY, username, username);
                User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
                Set<Server> serversSubscribes = user.getServers();
                for (Server server : serversSubscribes) {
                    redisService.subscribeUserToServer(server, username);
                }
            }else {
                session.close(CloseStatus.BAD_DATA);
            }
        }
    
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String username = (String) session.getAttributes().get("username");
            if (username != null) {
                System.out.println("Mensagem recebida de " + username + ": " + message.getPayload());
                String payload = message.getPayload();
                String[] parts = payload.split(":", 4);
                String channel = parts[0];
                redisService.sendMessageToChannel(channel, message.getPayload());
            } else {
                session.sendMessage(new TextMessage("Você não está autenticado."));
            }
    
        }
    
        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            String username = (String) session.getAttributes().get("username");

            sessionService.removeSession(username);
            redisService.getRedisTemplate().opsForHash().delete(REDIS_SESSIONS_KEY, username);

            UserEvent event = new UserEvent("USER_DISCONNECTED", username, "User has left");
            
        } 
}