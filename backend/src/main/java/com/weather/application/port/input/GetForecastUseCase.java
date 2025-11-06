package com.weather.application.port.input;

import com.weather.domain.model.Forecast;

import java.util.List;

/**
 * Input port for retrieving weather forecasts
 */
public interface GetForecastUseCase {

    /**
     * Gets weather forecast for specified days
     * @param city City name
     * @param country Country code (optional)
     * @param days Number of days (1-7)
     * @return List of forecasts
     */
    List<Forecast> getForecast(String city, String country, int days);
}
