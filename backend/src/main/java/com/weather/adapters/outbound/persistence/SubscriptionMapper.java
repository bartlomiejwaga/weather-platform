package com.weather.adapters.outbound.persistence;

import com.weather.adapters.outbound.persistence.entity.SubscriptionEntity;
import com.weather.domain.model.Location;
import com.weather.domain.model.Subscription;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SubscriptionMapper {

    public static SubscriptionEntity toEntity(Subscription domain) {
        if (domain == null) return null;

        Location loc = domain.getLocation();
        String alertTypesStr = domain.getAlertTypes() != null
            ? domain.getAlertTypes().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","))
            : "";

        Subscription.AlertThresholds thresholds = domain.getThresholds();

        return SubscriptionEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .email(domain.getEmail())
            .city(loc != null ? loc.getCity() : null)
            .country(loc != null ? loc.getCountry() : null)
            .latitude(loc != null ? loc.getLatitude() : null)
            .longitude(loc != null ? loc.getLongitude() : null)
            .alertTypes(alertTypesStr)
            .maxTemperature(thresholds != null ? thresholds.getMaxTemperature() : null)
            .minTemperature(thresholds != null ? thresholds.getMinTemperature() : null)
            .maxAQI(thresholds != null ? thresholds.getMaxAQI() : null)
            .maxWindSpeed(thresholds != null ? thresholds.getMaxWindSpeed() : null)
            .maxPrecipitation(thresholds != null ? thresholds.getMaxPrecipitation() : null)
            .maxUVIndex(thresholds != null ? thresholds.getMaxUVIndex() : null)
            .active(domain.isActive())
            .createdAt(domain.getCreatedAt())
            .lastNotifiedAt(domain.getLastNotifiedAt())
            .build();
    }

    public static Subscription toDomain(SubscriptionEntity entity) {
        if (entity == null) return null;

        Location location = Location.builder()
            .city(entity.getCity())
            .country(entity.getCountry())
            .latitude(entity.getLatitude())
            .longitude(entity.getLongitude())
            .build();

        Set<Subscription.AlertType> alertTypes = entity.getAlertTypes() != null && !entity.getAlertTypes().isEmpty()
            ? Arrays.stream(entity.getAlertTypes().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Subscription.AlertType::valueOf)
                .collect(Collectors.toSet())
            : Set.of();

        Subscription.AlertThresholds thresholds = Subscription.AlertThresholds.builder()
            .maxTemperature(entity.getMaxTemperature())
            .minTemperature(entity.getMinTemperature())
            .maxAQI(entity.getMaxAQI())
            .maxWindSpeed(entity.getMaxWindSpeed())
            .maxPrecipitation(entity.getMaxPrecipitation())
            .maxUVIndex(entity.getMaxUVIndex())
            .build();

        return Subscription.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .email(entity.getEmail())
            .location(location)
            .alertTypes(alertTypes)
            .thresholds(thresholds)
            .active(entity.getActive())
            .createdAt(entity.getCreatedAt())
            .lastNotifiedAt(entity.getLastNotifiedAt())
            .build();
    }
}
