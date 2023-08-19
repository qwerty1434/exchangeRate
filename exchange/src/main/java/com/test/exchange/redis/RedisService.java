package com.test.exchange.redis;

import com.test.exchange.apilayer.dto.ExchangeRateResponse;
import com.test.exchange.global.exception.RedisLockAcquisitionFailedException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.test.exchange.global.exception.ErrorMessage.REDIS_LOCK_ACQUISITION_ERROR;

@Service
@RequiredArgsConstructor
public class RedisService {
    @Value("${spring.redis.global-lock-name}")
    private String GLOBAL_LOCK_NAME;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, ExchangeRateResponse> redisTemplate;

    public void setValues(String key, ExchangeRateResponse value, Duration duration) {
        RLock lock = redissonClient.getLock(GLOBAL_LOCK_NAME);
        try{
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
            if(!available) {
                throw new RedisLockAcquisitionFailedException(REDIS_LOCK_ACQUISITION_ERROR);
            }
            ValueOperations<String, ExchangeRateResponse> values = redisTemplate.opsForValue();
            values.set(key, value, duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    public ExchangeRateResponse getValues(String key) {
        ValueOperations<String, ExchangeRateResponse> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public boolean isExists(String key){
        return redisTemplate.hasKey(key);
    }
}