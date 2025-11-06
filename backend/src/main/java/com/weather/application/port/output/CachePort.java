package com.weather.application.port.output;

import java.time.Duration;
import java.util.Optional;

/**
 * Output port for caching operations
 */
public interface CachePort {

    /**
     * Stores value in cache with TTL
     */
    <T> void put(String key, T value, Duration ttl);

    /**
     * Retrieves value from cache
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * Removes value from cache
     */
    void evict(String key);

    /**
     * Clears all cache entries matching pattern
     */
    void evictPattern(String pattern);

    /**
     * Checks if key exists in cache
     */
    boolean exists(String key);
}
