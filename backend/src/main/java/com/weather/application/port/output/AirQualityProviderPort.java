package com.weather.application.port.output;

import com.weather.domain.model.AQIReading;

import java.util.Optional;

/**
 * Output port for air quality data providers
 */
public interface AirQualityProviderPort {

    /**
     * Fetches current air quality data
     */
    Optional<AQIReading> getCurrentAirQuality(String city, String country);

    /**
     * Checks if the provider is available
     */
    boolean isAvailable();

    /**
     * Returns provider name for logging
     */
    String getProviderName();
}
