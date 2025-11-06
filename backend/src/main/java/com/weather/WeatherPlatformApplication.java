package com.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Weather Platform
 *
 * Features:
 * - Hexagonal architecture (Ports & Adapters)
 * - Multi-source weather data (APIs + Web scraping fallback)
 * - Caching with Redis
 * - Alert subscriptions with notifications
 * - Historical data tracking
 * - Resilience patterns (circuit breaker, retry, rate limiting)
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class WeatherPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherPlatformApplication.class, args);
    }
}
