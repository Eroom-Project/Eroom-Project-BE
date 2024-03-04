package com.sparta.eroomprojectbe.global.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {
    private final RedisTemplate redisTemplate;

    public RedisService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValues(String email, String refreshToken, Duration duration){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(email, refreshToken, duration);
    }

    public String getValues(String email){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(email);
    }

    public void delValues(String email){
        redisTemplate.delete(email);
    }

}
