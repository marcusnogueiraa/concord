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
import com.concord.concordapi.websocket.entity.ClientMessage;
import com.concord.concordapi.websocket.service.RedisService;
import com.concord.concordapi.websocket.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;


@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                String payload = message.getPayload();
                ClientMessage clientMessage = objectMapper.readValue(payload, ClientMessage.class);
                clientMessage.setUsername(username);
                redisService.sendToChannel(clientMessage.getTo(), clientMessage);
                
            } else {
                session.sendMessage(new TextMessage("Você não está autenticado."));
            }
    
        }
    
        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            String username = (String) session.getAttributes().get("username");
            if (username != null) {
                User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
                Set<Server> serversSubscribes = user.getServers();
                for (Server server : serversSubscribes) {
                    redisService.unsubscribeUserToServer(server, username);
                }
                sessionService.removeSession(username);
                redisService.getRedisTemplate().opsForHash().delete(REDIS_SESSIONS_KEY, username);
            }else {
                session.close(CloseStatus.BAD_DATA);
            }
        } 
}