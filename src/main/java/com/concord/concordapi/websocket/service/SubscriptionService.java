package com.concord.concordapi.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import com.concord.concordapi.shared.listener.RedisMessageListener;

@Service
public class SubscriptionService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    private RedisMessageListener redisMessageListener;

    private static final String REDIS_CHANNEL_PREFIX = "server:";

    // Inscreve o usuário nos canais de um servidor
    public void subscribeUserToServer(String username, Long serverId) {
        System.out.println("tentando inscrever "+username+" no servidor "+serverId);
        // Define o padrão do canal para todos os canais do servidor (com o formato "server:serverId:*")
        String serverChannelPattern = REDIS_CHANNEL_PREFIX + serverId + ":*";  // Todos os canais desse servidor
        
        // Inscreve o RedisMessageListenerContainer no padrão do canal
        redisMessageListenerContainer.addMessageListener(
            redisMessageListener, new PatternTopic(serverChannelPattern)
        );
        
        // Opcional: Registra o usuário em um "canal" específico ou algum mecanismo que indique que o usuário se inscreveu
        // Isso pode ser um banco de dados ou um registro interno para rastrear as inscrições

        // Para fins de demonstração, vamos apenas enviar uma mensagem para o canal de inscrição
        redisTemplate.convertAndSend(serverChannelPattern, username + " has joined the server "+serverId);
    }

    // Inscrever um usuário em um canal específico
    public void subscribeUserToChannel(String username, String serverId, String channelName) {
        String redisChannel = REDIS_CHANNEL_PREFIX + serverId + ":" + channelName;
        
        // Inscrever o usuário diretamente no canal
        redisMessageListenerContainer.addMessageListener(
            redisMessageListener, new PatternTopic(redisChannel)
        );
        
        // Também podemos adicionar algum código de rastreamento aqui, se necessário
        redisTemplate.convertAndSend(redisChannel, username + " has joined the channel.");
    }
}
