package com.weather.adapters.outbound.cache;

import com.weather.application.port.output.CachePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * Redis implementation of cache port
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheAdapter implements CachePort {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
            log.debug("Cached value for key: {} with TTL: {}s", key, ttl.getSeconds());
        } catch (Exception e) {
            log.error("Error caching value for key {}: {}", key, e.getMessage());
        }
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                log.debug("Cache miss for key: {}", key);
                return Optional.empty();
            }
            T value = objectMapper.readValue(json, type);
            log.debug("Cache hit for key: {}", key);
            return Optional.of(value);
        } catch (Exception e) {
            log.error("Error retrieving cached value for key {}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void evict(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Evicted cache key: {}", key);
        } catch (Exception e) {
            log.error("Error evicting cache key {}: {}", key, e.getMessage());
        }
    }

    @Override
    public void evictPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Evicted {} keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.error("Error evicting keys with pattern {}: {}", pattern, e.getMessage());
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Error checking existence of key {}: {}", key, e.getMessage());
            return false;
        }
    }
}
