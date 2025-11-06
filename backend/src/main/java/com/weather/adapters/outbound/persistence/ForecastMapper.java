package com.weather.adapters.outbound.persistence;

import com.weather.adapters.outbound.persistence.entity.ForecastEntity;
import com.weather.domain.model.Forecast;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;

public class ForecastMapper {

    public static ForecastEntity toEntity(Forecast domain) {
        if (domain == null) return null;

        Location loc = domain.getLocation();
        String locationKey = loc != null ? loc.getLocationKey() : "";

        return ForecastEntity.builder()
            .id(domain.getId())
            .locationKey(locationKey)
            .city(loc != null ? loc.getCity() : null)
            .country(loc != null ? loc.getCountry() : null)
            .latitude(loc != null ? loc.getLatitude() : null)
            .longitude(loc != null ? loc.getLongitude() : null)
            .forecastDate(domain.getDate())
            .tempMin(domain.getTempMin())
            .tempMax(domain.getTempMax())
            .tempAvg(domain.getTempAvg())
            .humidity(domain.getHumidity())
            .windSpeed(domain.getWindSpeed())
            .weatherCondition(domain.getWeatherCondition())
            .weatherDescription(domain.getWeatherDescription())
            .weatherIcon(domain.getWeatherIcon())
            .precipitationProbability(domain.getPrecipitationProbability())
            .precipitationAmount(domain.getPrecipitationAmount())
            .cloudiness(domain.getCloudiness())
            .uvIndex(domain.getUvIndex())
            .sunrise(domain.getSunrise())
            .sunset(domain.getSunset())
            .dataSource(mapDataSource(domain.getDataSource()))
            .createdAt(domain.getCreatedAt())
            .build();
    }

    public static Forecast toDomain(ForecastEntity entity) {
        if (entity == null) return null;

        Location location = Location.builder()
            .city(entity.getCity())
            .country(entity.getCountry())
            .latitude(entity.getLatitude())
            .longitude(entity.getLongitude())
            .build();

        return Forecast.builder()
            .id(entity.getId())
            .location(location)
            .date(entity.getForecastDate())
            .tempMin(entity.getTempMin())
            .tempMax(entity.getTempMax())
            .tempAvg(entity.getTempAvg())
            .humidity(entity.getHumidity())
            .windSpeed(entity.getWindSpeed())
            .weatherCondition(entity.getWeatherCondition())
            .weatherDescription(entity.getWeatherDescription())
            .weatherIcon(entity.getWeatherIcon())
            .precipitationProbability(entity.getPrecipitationProbability())
            .precipitationAmount(entity.getPrecipitationAmount())
            .cloudiness(entity.getCloudiness())
            .uvIndex(entity.getUvIndex())
            .sunrise(entity.getSunrise())
            .sunset(entity.getSunset())
            .dataSource(mapDataSource(entity.getDataSource()))
            .createdAt(entity.getCreatedAt())
            .build();
    }

    private static ForecastEntity.DataSourceType mapDataSource(WeatherReading.DataSource source) {
        if (source == null) return null;
        return switch (source) {
            case OPENWEATHER_API -> ForecastEntity.DataSourceType.OPENWEATHER_API;
            case IQAIR_API -> ForecastEntity.DataSourceType.IQAIR_API;
            case SCRAPER_FALLBACK -> ForecastEntity.DataSourceType.SCRAPER_FALLBACK;
            case CACHED -> ForecastEntity.DataSourceType.CACHED;
        };
    }

    private static WeatherReading.DataSource mapDataSource(ForecastEntity.DataSourceType source) {
        if (source == null) return null;
        return switch (source) {
            case OPENWEATHER_API -> WeatherReading.DataSource.OPENWEATHER_API;
            case IQAIR_API -> WeatherReading.DataSource.IQAIR_API;
            case SCRAPER_FALLBACK -> WeatherReading.DataSource.SCRAPER_FALLBACK;
            case CACHED -> WeatherReading.DataSource.CACHED;
        };
    }
}
