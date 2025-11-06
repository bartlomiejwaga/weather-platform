package com.weather.adapters.inbound.rest.dto;

import com.weather.domain.model.Forecast;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO for forecast API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResponseDTO {

    private Long id;
    private LocationDTO location;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private TemperatureDTO temperature;
    private WeatherConditionDTO weatherCondition;
    private PrecipitationDTO precipitation;
    private WindDTO wind;
    private SunDTO sun;
    private Integer humidity;
    private Integer cloudiness;
    private Integer uvIndex;
    private String dataSource;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

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
    public static class TemperatureDTO {
        private Double min;
        private Double max;
        private Double avg;
        private String unit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherConditionDTO {
        private String condition;
        private String description;
        private String icon;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrecipitationDTO {
        private Double probability;
        private Double amount;
        private boolean likelyToRain;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WindDTO {
        private Double speed;
        private String unit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SunDTO {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private Instant sunrise;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private Instant sunset;
    }

    /**
     * Maps domain model to DTO
     */
    public static ForecastResponseDTO fromDomain(Forecast forecast) {
        return ForecastResponseDTO.builder()
            .id(forecast.getId())
            .location(forecast.getLocation() != null ? LocationDTO.builder()
                .city(forecast.getLocation().getCity())
                .country(forecast.getLocation().getCountry())
                .latitude(forecast.getLocation().getLatitude())
                .longitude(forecast.getLocation().getLongitude())
                .build() : null)
            .date(forecast.getDate())
            .temperature(TemperatureDTO.builder()
                .min(forecast.getTempMin())
                .max(forecast.getTempMax())
                .avg(forecast.getTempAvg())
                .unit("celsius")
                .build())
            .weatherCondition(WeatherConditionDTO.builder()
                .condition(forecast.getWeatherCondition())
                .description(forecast.getWeatherDescription())
                .icon(forecast.getWeatherIcon())
                .build())
            .precipitation(PrecipitationDTO.builder()
                .probability(forecast.getPrecipitationProbability())
                .amount(forecast.getPrecipitationAmount())
                .likelyToRain(forecast.isLikelyToRain())
                .build())
            .wind(WindDTO.builder()
                .speed(forecast.getWindSpeed())
                .unit("m/s")
                .build())
            .sun(SunDTO.builder()
                .sunrise(forecast.getSunrise())
                .sunset(forecast.getSunset())
                .build())
            .humidity(forecast.getHumidity())
            .cloudiness(forecast.getCloudiness())
            .uvIndex(forecast.getUvIndex())
            .dataSource(forecast.getDataSource() != null ? forecast.getDataSource().name() : null)
            .createdAt(forecast.getCreatedAt())
            .build();
    }
}
