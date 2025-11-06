package com.weather.adapters.outbound.persistence;

import com.weather.adapters.outbound.persistence.entity.AQIReadingEntity;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;

public class AQIReadingMapper {

    public static AQIReadingEntity toEntity(AQIReading domain) {
        if (domain == null) return null;

        Location loc = domain.getLocation();
        String locationKey = loc != null ? loc.getLocationKey() : "";

        return AQIReadingEntity.builder()
            .id(domain.getId())
            .locationKey(locationKey)
            .city(loc != null ? loc.getCity() : null)
            .country(loc != null ? loc.getCountry() : null)
            .latitude(loc != null ? loc.getLatitude() : null)
            .longitude(loc != null ? loc.getLongitude() : null)
            .timestamp(domain.getTimestamp())
            .aqi(domain.getAqi())
            .level(mapLevel(domain.getLevel()))
            .pm25(domain.getPm25())
            .pm10(domain.getPm10())
            .co(domain.getCo())
            .no2(domain.getNo2())
            .so2(domain.getSo2())
            .o3(domain.getO3())
            .dataSource(mapDataSource(domain.getDataSource()))
            .createdAt(domain.getCreatedAt())
            .build();
    }

    public static AQIReading toDomain(AQIReadingEntity entity) {
        if (entity == null) return null;

        Location location = Location.builder()
            .city(entity.getCity())
            .country(entity.getCountry())
            .latitude(entity.getLatitude())
            .longitude(entity.getLongitude())
            .build();

        return AQIReading.builder()
            .id(entity.getId())
            .location(location)
            .timestamp(entity.getTimestamp())
            .aqi(entity.getAqi())
            .level(mapLevel(entity.getLevel()))
            .pm25(entity.getPm25())
            .pm10(entity.getPm10())
            .co(entity.getCo())
            .no2(entity.getNo2())
            .so2(entity.getSo2())
            .o3(entity.getO3())
            .dataSource(mapDataSource(entity.getDataSource()))
            .createdAt(entity.getCreatedAt())
            .build();
    }

    private static AQIReadingEntity.AQILevelType mapLevel(AQIReading.AQILevel level) {
        if (level == null) return null;
        return switch (level) {
            case GOOD -> AQIReadingEntity.AQILevelType.GOOD;
            case MODERATE -> AQIReadingEntity.AQILevelType.MODERATE;
            case UNHEALTHY_SENSITIVE -> AQIReadingEntity.AQILevelType.UNHEALTHY_SENSITIVE;
            case UNHEALTHY -> AQIReadingEntity.AQILevelType.UNHEALTHY;
            case VERY_UNHEALTHY -> AQIReadingEntity.AQILevelType.VERY_UNHEALTHY;
            case HAZARDOUS -> AQIReadingEntity.AQILevelType.HAZARDOUS;
        };
    }

    private static AQIReading.AQILevel mapLevel(AQIReadingEntity.AQILevelType level) {
        if (level == null) return null;
        return switch (level) {
            case GOOD -> AQIReading.AQILevel.GOOD;
            case MODERATE -> AQIReading.AQILevel.MODERATE;
            case UNHEALTHY_SENSITIVE -> AQIReading.AQILevel.UNHEALTHY_SENSITIVE;
            case UNHEALTHY -> AQIReading.AQILevel.UNHEALTHY;
            case VERY_UNHEALTHY -> AQIReading.AQILevel.VERY_UNHEALTHY;
            case HAZARDOUS -> AQIReading.AQILevel.HAZARDOUS;
        };
    }

    private static AQIReadingEntity.DataSourceType mapDataSource(WeatherReading.DataSource source) {
        if (source == null) return null;
        return switch (source) {
            case OPENWEATHER_API -> AQIReadingEntity.DataSourceType.OPENWEATHER_API;
            case IQAIR_API -> AQIReadingEntity.DataSourceType.IQAIR_API;
            case SCRAPER_FALLBACK -> AQIReadingEntity.DataSourceType.SCRAPER_FALLBACK;
            case CACHED -> AQIReadingEntity.DataSourceType.CACHED;
        };
    }

    private static WeatherReading.DataSource mapDataSource(AQIReadingEntity.DataSourceType source) {
        if (source == null) return null;
        return switch (source) {
            case OPENWEATHER_API -> WeatherReading.DataSource.OPENWEATHER_API;
            case IQAIR_API -> WeatherReading.DataSource.IQAIR_API;
            case SCRAPER_FALLBACK -> WeatherReading.DataSource.SCRAPER_FALLBACK;
            case CACHED -> WeatherReading.DataSource.CACHED;
        };
    }
}
