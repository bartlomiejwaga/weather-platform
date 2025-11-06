package com.weather.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Domain entity representing Air Quality Index reading
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AQIReading {
    private Long id;
    private Location location;
    private Instant timestamp;
    private Integer aqi;
    private AQILevel level;
    private Double pm25;
    private Double pm10;
    private Double co;
    private Double no2;
    private Double so2;
    private Double o3;
    private WeatherReading.DataSource dataSource;
    private Instant createdAt;

    /**
     * AQI level categorization following EPA standards
     */
    public enum AQILevel {
        GOOD(0, 50, "Good"),
        MODERATE(51, 100, "Moderate"),
        UNHEALTHY_SENSITIVE(101, 150, "Unhealthy for Sensitive Groups"),
        UNHEALTHY(151, 200, "Unhealthy"),
        VERY_UNHEALTHY(201, 300, "Very Unhealthy"),
        HAZARDOUS(301, 500, "Hazardous");

        private final int min;
        private final int max;
        private final String description;

        AQILevel(int min, int max, String description) {
            this.min = min;
            this.max = max;
            this.description = description;
        }

        public int getMin() { return min; }
        public int getMax() { return max; }
        public String getDescription() { return description; }

        public static AQILevel fromAQI(int aqi) {
            for (AQILevel level : values()) {
                if (aqi >= level.min && aqi <= level.max) {
                    return level;
                }
            }
            return HAZARDOUS;
        }
    }

    /**
     * Auto-calculates level from AQI value
     */
    public AQILevel getLevel() {
        if (level == null && aqi != null) {
            return AQILevel.fromAQI(aqi);
        }
        return level;
    }

    /**
     * Checks if air quality requires alert
     */
    public boolean requiresAlert() {
        AQILevel currentLevel = getLevel();
        return currentLevel == AQILevel.UNHEALTHY
            || currentLevel == AQILevel.VERY_UNHEALTHY
            || currentLevel == AQILevel.HAZARDOUS;
    }
}
