package com.concord.concordapi.websocket.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.websocket.entity.ClientMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RedisService {

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String SERVER_USERS_KEY_PATTERN = "server:%s:users";

    public void sendToChannel(String to, ClientMessage clientMessage) {
        
        try {
            String jsonMessage = objectMapper.writeValueAsString(clientMessage);
            redisTemplate.convertAndSend("server:"+to, jsonMessage);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    public RedisTemplate<String, String> getRedisTemplate(){
        return redisTemplate;
    }

    public void subscribeUserToServer(Server server, String username) {
        String serverKey = String.format(SERVER_USERS_KEY_PATTERN, server.getId());
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.add(serverKey, username);
    }
    public void unsubscribeUserToServer(Server server, String username) {
        String serverKey = String.format(SERVER_USERS_KEY_PATTERN, server.getId());
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.remove(serverKey, username);
    }
    public boolean isUserSubscribedToServer(Server server, String username) {
        String serverKey = String.format(SERVER_USERS_KEY_PATTERN, server.getId());
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        return setOps.isMember(serverKey, username);
    }
    public Set<String> getUsersSubscribedToServer(String to) {
        String serverKey = String.format(SERVER_USERS_KEY_PATTERN, to);
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        return setOps.members(serverKey);
    }
    
}