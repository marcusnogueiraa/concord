package com.concord.concordapi.shared.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void save(String key, Object value, int durationInSeconds) {
        redisTemplate.opsForValue().set(key, value, durationInSeconds, TimeUnit.SECONDS);
    }

    public void saveIfDontExists(String key, Object value, int durationInSeconds){
        redisTemplate.opsForValue().setIfAbsent(key, value, durationInSeconds, TimeUnit.SECONDS);
    }

    public boolean exists(String key){
        Object result = redisTemplate.opsForValue().get(key);
        return result != null;
    }

    public Long increment(String key){
        return redisTemplate.opsForValue().increment(key);
    }

    public Object find(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
