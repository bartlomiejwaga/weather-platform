package com.weather.application.port.input;

import com.weather.domain.model.WeatherReading;
import com.weather.domain.model.AQIReading;

import java.time.Instant;
import java.util.List;

/**
 * Input port for retrieving historical weather data
 */
public interface GetHistoryUseCase {

    /**
     * Gets weather history for a location
     */
    List<WeatherReading> getWeatherHistory(String city, String country, Instant from, Instant to);

    /**
     * Gets AQI history for a location
     */
    List<AQIReading> getAQIHistory(String city, String country, Instant from, Instant to);
}
