package com.test.exchange.redis;

import com.test.exchange.apilayer.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, ExchangeRateResponse> redisTemplate;

    public void setValues(String key, ExchangeRateResponse value, Duration duration) {
        ValueOperations<String, ExchangeRateResponse> values = redisTemplate.opsForValue();
        values.set(key, value, duration);
    }

    public ExchangeRateResponse getValues(String key) {
        ValueOperations<String, ExchangeRateResponse> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public boolean isExists(String key){
        return redisTemplate.hasKey(key);
    }
}