package com.concord.concordapi.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import com.concord.concordapi.websocket.listener.RedisMessageListener;

@Service
public class SubscriptionService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    private RedisMessageListener redisMessageListener;

    private static final String REDIS_CHANNEL_PREFIX = "server:";

    public void subscribeUserToServer(String username, Long serverId) {
       
        String serverChannelPattern = REDIS_CHANNEL_PREFIX + serverId + ":*";  
        
        // Inscreve o RedisMessageListenerContainer no padrão do canal
        redisMessageListenerContainer.addMessageListener(
            redisMessageListener, new PatternTopic(serverChannelPattern)
        );
        
        redisTemplate.convertAndSend(serverChannelPattern, username + " has joined the server "+serverId);
    }

}
