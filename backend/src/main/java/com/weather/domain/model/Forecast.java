package com.weather.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Instant;

/**
 * Domain entity representing weather forecast for a specific date
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forecast {
    private Long id;
    private Location location;
    private LocalDate date;
    private Double tempMin;
    private Double tempMax;
    private Double tempAvg;
    private Integer humidity;
    private Double windSpeed;
    private String weatherCondition;
    private String weatherDescription;
    private String weatherIcon;
    private Double precipitationProbability;
    private Double precipitationAmount;
    private Integer cloudiness;
    private Integer uvIndex;
    private Instant sunrise;
    private Instant sunset;
    private WeatherReading.DataSource dataSource;
    private Instant createdAt;

    /**
     * Checks if it's likely to rain (precipitation probability > 50%)
     */
    public boolean isLikelyToRain() {
        return precipitationProbability != null && precipitationProbability > 50.0;
    }

    /**
     * Checks if UV index is high (>= 6)
     */
    public boolean hasHighUV() {
        return uvIndex != null && uvIndex >= 6;
    }

    /**
     * Returns temperature range as string
     */
    public String getTemperatureRange() {
        if (tempMin == null || tempMax == null) return "N/A";
        return String.format("%.1f°C - %.1f°C", tempMin, tempMax);
    }
}
