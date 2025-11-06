package com.weather.adapters.inbound.rest.dto;

import com.weather.domain.model.Subscription;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

/**
 * DTO for subscription API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDTO {

    private Long id;
    private String userId;
    private String email;
    private LocationDTO location;
    private Set<Subscription.AlertType> alertTypes;
    private AlertThresholdsDTO thresholds;
    private boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant lastNotifiedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private String city;
        private String country;
        private Double latitude;
        private Double longitude;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertThresholdsDTO {
        private Double maxTemperature;
        private Double minTemperature;
        private Integer maxAQI;
        private Double maxWindSpeed;
        private Double maxPrecipitation;
        private Integer maxUVIndex;
    }

    /**
     * Maps domain model to DTO
     */
    public static SubscriptionResponseDTO fromDomain(Subscription subscription) {
        return SubscriptionResponseDTO.builder()
            .id(subscription.getId())
            .userId(subscription.getUserId())
            .email(subscription.getEmail())
            .location(subscription.getLocation() != null ? LocationDTO.builder()
                .city(subscription.getLocation().getCity())
                .country(subscription.getLocation().getCountry())
                .latitude(subscription.getLocation().getLatitude())
                .longitude(subscription.getLocation().getLongitude())
                .build() : null)
            .alertTypes(subscription.getAlertTypes())
            .thresholds(subscription.getThresholds() != null ? AlertThresholdsDTO.builder()
                .maxTemperature(subscription.getThresholds().getMaxTemperature())
                .minTemperature(subscription.getThresholds().getMinTemperature())
                .maxAQI(subscription.getThresholds().getMaxAQI())
                .maxWindSpeed(subscription.getThresholds().getMaxWindSpeed())
                .maxPrecipitation(subscription.getThresholds().getMaxPrecipitation())
                .maxUVIndex(subscription.getThresholds().getMaxUVIndex())
                .build() : null)
            .active(subscription.isActive())
            .createdAt(subscription.getCreatedAt())
            .lastNotifiedAt(subscription.getLastNotifiedAt())
            .build();
    }
}
