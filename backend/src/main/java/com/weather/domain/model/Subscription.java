package com.weather.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

/**
 * Domain entity representing a user's alert subscription
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    private Long id;
    private String userId;
    private String email;
    private Location location;
    private Set<AlertType> alertTypes;
    private AlertThresholds thresholds;
    private boolean active;
    private Instant createdAt;
    private Instant lastNotifiedAt;

    /**
     * Types of alerts users can subscribe to
     */
    public enum AlertType {
        HIGH_TEMPERATURE,
        LOW_TEMPERATURE,
        POOR_AIR_QUALITY,
        EXTREME_WEATHER,
        HIGH_WIND,
        HEAVY_RAIN,
        UV_WARNING
    }

    /**
     * Thresholds for triggering alerts
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertThresholds {
        private Double maxTemperature;
        private Double minTemperature;
        private Integer maxAQI;
        private Double maxWindSpeed;
        private Double maxPrecipitation;
        private Integer maxUVIndex;
    }

    /**
     * Checks if alert should be sent based on weather reading
     */
    public boolean shouldAlert(WeatherReading reading) {
        if (!active || thresholds == null) return false;

        if (alertTypes.contains(AlertType.HIGH_TEMPERATURE)
            && thresholds.getMaxTemperature() != null
            && reading.getTemperatureCelsius() > thresholds.getMaxTemperature()) {
            return true;
        }

        if (alertTypes.contains(AlertType.LOW_TEMPERATURE)
            && thresholds.getMinTemperature() != null
            && reading.getTemperatureCelsius() < thresholds.getMinTemperature()) {
            return true;
        }

        if (alertTypes.contains(AlertType.HIGH_WIND)
            && thresholds.getMaxWindSpeed() != null
            && reading.getWindSpeed() > thresholds.getMaxWindSpeed()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if alert should be sent based on AQI reading
     */
    public boolean shouldAlert(AQIReading reading) {
        if (!active || thresholds == null) return false;

        return alertTypes.contains(AlertType.POOR_AIR_QUALITY)
            && thresholds.getMaxAQI() != null
            && reading.getAqi() > thresholds.getMaxAQI();
    }

    /**
     * Checks if enough time has passed since last notification (rate limiting)
     */
    public boolean canSendNotification(long cooldownMinutes) {
        if (lastNotifiedAt == null) return true;
        Instant cooldownThreshold = Instant.now().minusSeconds(cooldownMinutes * 60);
        return lastNotifiedAt.isBefore(cooldownThreshold);
    }
}
