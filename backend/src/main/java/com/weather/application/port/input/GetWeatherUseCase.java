package com.weather.application.port.input;

import com.weather.domain.model.WeatherReading;
import com.weather.domain.model.AQIReading;

/**
 * Input port for retrieving current weather data.
 * This defines the interface that the application layer exposes.
 */
public interface GetWeatherUseCase {

    /**
     * Gets current weather for a city
     * @param city City name
     * @param country Country code (optional)
     * @return Weather reading with AQI data
     */
    WeatherResponse getCurrentWeather(String city, String country);

    /**
     * Response object containing weather and air quality data
     */
    record WeatherResponse(
        WeatherReading weather,
        AQIReading airQuality,
        boolean fromCache
    ) {}
}
