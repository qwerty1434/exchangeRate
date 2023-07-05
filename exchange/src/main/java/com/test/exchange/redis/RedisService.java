package com.test.exchange.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public void setValues(String key, Double value, Duration duration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, Double.toString(value), duration);
    }

    public double getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return Double.parseDouble(values.get(key));
    }

    public boolean isExists(String key){
        return redisTemplate.hasKey(key);
    }
}