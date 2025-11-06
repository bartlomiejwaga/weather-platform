package com.weather.adapters.inbound.rest.dto;

import com.weather.application.port.input.GetWeatherUseCase.WeatherResponse;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.WeatherReading;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for weather API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponseDTO {

    private LocationDTO location;
    private CurrentWeatherDTO weather;
    private AirQualityDTO airQuality;
    private MetadataDTO metadata;

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
    public static class CurrentWeatherDTO {
        private Double temperature;
        private String temperatureUnit;
        private Double humidity;
        private Double pressure;
        private Double windSpeed;
        private Integer windDirection;
        private String condition;
        private String description;
        private String icon;
        private Double visibility;
        private Integer cloudiness;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private Instant timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirQualityDTO {
        private Integer aqi;
        private String level;
        private String levelDescription;
        private Double pm25;
        private Double pm10;
        private Double co;
        private Double no2;
        private Double so2;
        private Double o3;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private Instant timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetadataDTO {
        private String dataSource;
        private boolean fromCache;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private Instant retrievedAt;
    }

    /**
     * Maps domain model to DTO
     */
    public static WeatherResponseDTO fromDomain(WeatherResponse response) {
        WeatherReading weather = response.weather();
        AQIReading aqi = response.airQuality();

        return WeatherResponseDTO.builder()
            .location(weather != null && weather.getLocation() != null ? LocationDTO.builder()
                .city(weather.getLocation().getCity())
                .country(weather.getLocation().getCountry())
                .latitude(weather.getLocation().getLatitude())
                .longitude(weather.getLocation().getLongitude())
                .build() : null)
            .weather(weather != null ? CurrentWeatherDTO.builder()
                .temperature(weather.getTemperatureCelsius())
                .temperatureUnit("celsius")
                .humidity(weather.getHumidity())
                .pressure(weather.getPressure())
                .windSpeed(weather.getWindSpeed())
                .windDirection(weather.getWindDirection())
                .condition(weather.getWeatherCondition())
                .description(weather.getWeatherDescription())
                .icon(weather.getWeatherIcon())
                .visibility(weather.getVisibility())
                .cloudiness(weather.getCloudiness())
                .timestamp(weather.getTimestamp())
                .build() : null)
            .airQuality(aqi != null ? AirQualityDTO.builder()
                .aqi(aqi.getAqi())
                .level(aqi.getLevel() != null ? aqi.getLevel().name() : null)
                .levelDescription(aqi.getLevel() != null ? aqi.getLevel().getDescription() : null)
                .pm25(aqi.getPm25())
                .pm10(aqi.getPm10())
                .co(aqi.getCo())
                .no2(aqi.getNo2())
                .so2(aqi.getSo2())
                .o3(aqi.getO3())
                .timestamp(aqi.getTimestamp())
                .build() : null)
            .metadata(MetadataDTO.builder()
                .dataSource(weather != null && weather.getDataSource() != null
                    ? weather.getDataSource().name() : "UNKNOWN")
                .fromCache(response.fromCache())
                .retrievedAt(Instant.now())
                .build())
            .build();
    }
}
