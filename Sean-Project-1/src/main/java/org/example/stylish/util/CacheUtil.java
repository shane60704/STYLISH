package org.example.stylish.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.QueryTimeoutException;
import java.time.Duration;

@Component
public class CacheUtil {

    private static final Logger log = LoggerFactory.getLogger(CacheUtil.class);
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public CacheUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getFromCache(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (RedisConnectionFailureException e) {
            throw new RedisConnectionFailureException("Redis connection failed", e);
        } catch (QueryTimeoutException e) {
            throw new QueryTimeoutException("Redis query timeout", e);
        }
    }

    public void saveToCache(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (RedisConnectionFailureException e) {
            throw new RedisConnectionFailureException("Redis connection failed", e);
        } catch (QueryTimeoutException e) {
            throw new QueryTimeoutException("Redis query timeout", e);
        }
    }

    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (RedisConnectionFailureException e) {
            throw new RedisConnectionFailureException("Redis connection failed", e);
        } catch (QueryTimeoutException e) {
            throw new QueryTimeoutException("Redis query timeout", e);
        }
    }

    public void deleteFromCache(String key) {
        try {
            redisTemplate.delete(key);
        } catch (RedisConnectionFailureException e) {
            throw new RedisConnectionFailureException("Redis connection failed", e);
        } catch (QueryTimeoutException e) {
            throw new QueryTimeoutException("Redis query timeout", e);
        }
    }

    public Long increment(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (RedisConnectionFailureException e) {
            throw new RedisConnectionFailureException("Redis connection failed", e);
        } catch (QueryTimeoutException e) {
            throw new QueryTimeoutException("Redis query timeout", e);
        }
    }

    public void expire(String key, Duration duration) {
        try {
            redisTemplate.expire(key, duration);
        } catch (RedisConnectionFailureException e) {
            throw new RedisConnectionFailureException("Redis connection failed", e);
        } catch (QueryTimeoutException e) {
            throw new QueryTimeoutException("Redis query timeout", e);
        }
    }

}
