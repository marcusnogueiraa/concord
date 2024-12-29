package com.concord.concordapi.websocket.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import com.concord.concordapi.server.entity.Server;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String SERVER_USERS_KEY_PATTERN = "servers:%s:users";

    public void sendMessageToChannel(String server, String message) {
        // Publica a mensagem no canal
        redisTemplate.convertAndSend("servers:"+server, message);
        
        // Para fins de depuração, você pode adicionar um log aqui
        System.out.println("Mensagem enviada para o canal " + server + ": " + message);
    }
    public RedisTemplate<String, String> getRedisTemplate(){
        return redisTemplate;
    }

    public void subscribeUserToServer(Server server, String username) {
        String serverKey = String.format(SERVER_USERS_KEY_PATTERN, server.getId());
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.add(serverKey, username);
        System.out.println("Usuario " + username + " inscrito no servidor " + server.getName());
    }

    public boolean isUserSubscribedToServer(Server server, String username) {
        String serverKey = String.format(SERVER_USERS_KEY_PATTERN, server.getId());
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        return setOps.isMember(serverKey, username);
    }
    public Set<String> getUsersSubscribedToServer(Long serverId) {
        String serverKey = String.format(SERVER_USERS_KEY_PATTERN, serverId);
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        return setOps.members(serverKey);
    }
    
}