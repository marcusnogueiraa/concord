package com.concord.concordapi.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.Topic;

import com.concord.concordapi.websocket.listener.RedisMessageListener;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.data.redis.listener.PatternTopic;
// import org.springframework.data.redis.listener.RedisMessageListenerContainer;
// import org.springframework.data.redis.listener.Topic;

// import com.concord.concordapi.websocket.listener.RedisMessageListener;

// @Configuration
// public class RedisConfiguration {

//     @Autowired
//     private RedisMessageListener redisMessageListener;

//     @Bean
//     public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory connectionFactory) {
//         RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//         container.setConnectionFactory(connectionFactory);
//         Topic topic = new PatternTopic("events");
//         container.addMessageListener(redisMessageListener, topic);
        
//         return container;
//     }

//     @Bean
//     public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//         return new StringRedisTemplate(redisConnectionFactory);
//     }
// }




@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory redisConnectionFactory, 
        RedisMessageListener messageListener    
    ){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener, new PatternTopic("servers:*"));
        return container;
    }
}