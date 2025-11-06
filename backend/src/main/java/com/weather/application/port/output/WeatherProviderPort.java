package com.weather.application.port.output;

import com.weather.domain.model.WeatherReading;
import com.weather.domain.model.Forecast;

import java.util.List;
import java.util.Optional;

/**
 * Output port for weather data providers (APIs)
 */
public interface WeatherProviderPort {

    /**
     * Fetches current weather from external API
     */
    Optional<WeatherReading> getCurrentWeather(String city, String country);

    /**
     * Fetches forecast from external API
     */
    Optional<List<Forecast>> getForecast(String city, String country, int days);

    /**
     * Checks if the provider is available
     */
    boolean isAvailable();

    /**
     * Returns provider name for logging
     */
    String getProviderName();
}
