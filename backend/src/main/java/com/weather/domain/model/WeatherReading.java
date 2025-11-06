package com.weather.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Domain entity representing a weather reading at a specific point in time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherReading {
    private Long id;
    private Location location;
    private Instant timestamp;
    private Double temperatureCelsius;
    private Double temperatureFahrenheit;
    private Double humidity;
    private Double pressure;
    private Double windSpeed;
    private Integer windDirection;
    private String weatherCondition;
    private String weatherDescription;
    private String weatherIcon;
    private Double visibility;
    private Integer cloudiness;
    private DataSource dataSource;
    private Instant createdAt;

    /**
     * Enum representing the source of weather data
     */
    public enum DataSource {
        OPENWEATHER_API,
        IQAIR_API,
        SCRAPER_FALLBACK,
        CACHED
    }

    /**
     * Converts temperature to Fahrenheit if only Celsius is available
     */
    public Double getTemperatureFahrenheit() {
        if (temperatureFahrenheit == null && temperatureCelsius != null) {
            return (temperatureCelsius * 9/5) + 32;
        }
        return temperatureFahrenheit;
    }

    /**
     * Converts temperature to Celsius if only Fahrenheit is available
     */
    public Double getTemperatureCelsius() {
        if (temperatureCelsius == null && temperatureFahrenheit != null) {
            return (temperatureFahrenheit - 32) * 5/9;
        }
        return temperatureCelsius;
    }

    /**
     * Checks if the reading is recent (within the last hour)
     */
    public boolean isRecent() {
        if (timestamp == null) return false;
        return timestamp.isAfter(Instant.now().minusSeconds(3600));
    }
}
