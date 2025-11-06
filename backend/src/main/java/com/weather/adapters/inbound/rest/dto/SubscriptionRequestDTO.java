package com.weather.adapters.inbound.rest.dto;

import com.weather.domain.model.Location;
import com.weather.domain.model.Subscription;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for subscription creation/update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Location is required")
    @Valid
    private LocationDTO location;

    @NotEmpty(message = "At least one alert type is required")
    private Set<Subscription.AlertType> alertTypes;

    @Valid
    private AlertThresholdsDTO thresholds;

    private boolean active;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        @NotBlank(message = "City is required")
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
     * Converts DTO to domain model
     */
    public Subscription toDomain() {
        return Subscription.builder()
            .userId(userId)
            .email(email)
            .location(location != null ? Location.builder()
                .city(location.getCity())
                .country(location.getCountry())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build() : null)
            .alertTypes(alertTypes)
            .thresholds(thresholds != null ? Subscription.AlertThresholds.builder()
                .maxTemperature(thresholds.getMaxTemperature())
                .minTemperature(thresholds.getMinTemperature())
                .maxAQI(thresholds.getMaxAQI())
                .maxWindSpeed(thresholds.getMaxWindSpeed())
                .maxPrecipitation(thresholds.getMaxPrecipitation())
                .maxUVIndex(thresholds.getMaxUVIndex())
                .build() : null)
            .active(active)
            .build();
    }
}
