package com.concord.concordapi.message.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.concord.concordapi.message.entity.Message;
import com.concord.concordapi.message.repository.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    // @Autowired
    // private StringRedisTemplate redisTemplate;

    // private static final String REDIS_CHANNEL_PREFIX = "server:";

    public void saveMessage(String serverId, String channelName, String sender, String content) {
        // Salva a mensagem no MongoDB
        Message message = new Message();
        message.setSender(sender);
        message.setChannel(channelName);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);

        // // Publica no Redis para que todos os inscritos no canal recebam a mensagem
        // String redisChannel = REDIS_CHANNEL_PREFIX + serverId + ":" + channelName;
        // redisTemplate.convertAndSend(redisChannel, content);
    }
}
