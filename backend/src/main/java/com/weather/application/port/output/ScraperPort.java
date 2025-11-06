package com.weather.application.port.output;

import com.weather.domain.model.WeatherReading;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.Forecast;

import java.util.List;
import java.util.Optional;

/**
 * Output port for web scraping fallback
 */
public interface ScraperPort {

    /**
     * Scrapes weather data from web sources
     */
    Optional<WeatherReading> scrapeWeather(String city, String country);

    /**
     * Scrapes air quality data from web sources
     */
    Optional<AQIReading> scrapeAirQuality(String city, String country);

    /**
     * Scrapes forecast data from web sources
     */
    Optional<List<Forecast>> scrapeForecast(String city, String country, int days);

    /**
     * Checks if scraping is enabled
     */
    boolean isEnabled();
}
