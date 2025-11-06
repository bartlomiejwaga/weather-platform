package com.weather.adapters.outbound.persistence;

import com.weather.adapters.outbound.persistence.entity.WeatherReadingEntity;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;

public class WeatherReadingMapper {

    public static WeatherReadingEntity toEntity(WeatherReading domain) {
        if (domain == null) return null;

        Location loc = domain.getLocation();
        String locationKey = loc != null ? loc.getLocationKey() : "";

        return WeatherReadingEntity.builder()
            .id(domain.getId())
            .locationKey(locationKey)
            .city(loc != null ? loc.getCity() : null)
            .country(loc != null ? loc.getCountry() : null)
            .latitude(loc != null ? loc.getLatitude() : null)
            .longitude(loc != null ? loc.getLongitude() : null)
            .timestamp(domain.getTimestamp())
            .temperatureCelsius(domain.getTemperatureCelsius())
            .temperatureFahrenheit(domain.getTemperatureFahrenheit())
            .humidity(domain.getHumidity())
            .pressure(domain.getPressure())
            .windSpeed(domain.getWindSpeed())
            .windDirection(domain.getWindDirection())
            .weatherCondition(domain.getWeatherCondition())
            .weatherDescription(domain.getWeatherDescription())
            .weatherIcon(domain.getWeatherIcon())
            .visibility(domain.getVisibility())
            .cloudiness(domain.getCloudiness())
            .dataSource(mapDataSource(domain.getDataSource()))
            .createdAt(domain.getCreatedAt())
            .build();
    }

    public static WeatherReading toDomain(WeatherReadingEntity entity) {
        if (entity == null) return null;

        Location location = Location.builder()
            .city(entity.getCity())
            .country(entity.getCountry())
            .latitude(entity.getLatitude())
            .longitude(entity.getLongitude())
            .build();

        return WeatherReading.builder()
            .id(entity.getId())
            .location(location)
            .timestamp(entity.getTimestamp())
            .temperatureCelsius(entity.getTemperatureCelsius())
            .temperatureFahrenheit(entity.getTemperatureFahrenheit())
            .humidity(entity.getHumidity())
            .pressure(entity.getPressure())
            .windSpeed(entity.getWindSpeed())
            .windDirection(entity.getWindDirection())
            .weatherCondition(entity.getWeatherCondition())
            .weatherDescription(entity.getWeatherDescription())
            .weatherIcon(entity.getWeatherIcon())
            .visibility(entity.getVisibility())
            .cloudiness(entity.getCloudiness())
            .dataSource(mapDataSource(entity.getDataSource()))
            .createdAt(entity.getCreatedAt())
            .build();
    }

    private static WeatherReadingEntity.DataSourceType mapDataSource(WeatherReading.DataSource source) {
        if (source == null) return null;
        return switch (source) {
            case OPENWEATHER_API -> WeatherReadingEntity.DataSourceType.OPENWEATHER_API;
            case IQAIR_API -> WeatherReadingEntity.DataSourceType.IQAIR_API;
            case SCRAPER_FALLBACK -> WeatherReadingEntity.DataSourceType.SCRAPER_FALLBACK;
            case CACHED -> WeatherReadingEntity.DataSourceType.CACHED;
        };
    }

    private static WeatherReading.DataSource mapDataSource(WeatherReadingEntity.DataSourceType source) {
        if (source == null) return null;
        return switch (source) {
            case OPENWEATHER_API -> WeatherReading.DataSource.OPENWEATHER_API;
            case IQAIR_API -> WeatherReading.DataSource.IQAIR_API;
            case SCRAPER_FALLBACK -> WeatherReading.DataSource.SCRAPER_FALLBACK;
            case CACHED -> WeatherReading.DataSource.CACHED;
        };
    }
}
