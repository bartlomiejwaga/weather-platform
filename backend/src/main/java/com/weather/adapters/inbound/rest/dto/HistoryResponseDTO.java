package com.weather.adapters.inbound.rest.dto;

import com.weather.domain.model.AQIReading;
import com.weather.domain.model.WeatherReading;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for history API responses
 */
public class HistoryResponseDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherHistoryResponseDTO {
        private LocationDTO location;
        private PeriodDTO period;
        private Integer count;
        private List<WeatherReadingDTO> readings;

        public static WeatherHistoryResponseDTO fromDomain(String city, String country,
                                                           Instant from, Instant to,
                                                           List<WeatherReading> readings) {
            return WeatherHistoryResponseDTO.builder()
                .location(LocationDTO.builder()
                    .city(city)
                    .country(country)
                    .build())
                .period(PeriodDTO.builder()
                    .from(from)
                    .to(to)
                    .build())
                .count(readings.size())
                .readings(readings.stream()
                    .map(WeatherReadingDTO::fromDomain)
                    .collect(Collectors.toList()))
                .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AQIHistoryResponseDTO {
        private LocationDTO location;
        private PeriodDTO period;
        private Integer count;
        private List<AQIReadingDTO> readings;

        public static AQIHistoryResponseDTO fromDomain(String city, String country,
                                                       Instant from, Instant to,
                                                       List<AQIReading> readings) {
            return AQIHistoryResponseDTO.builder()
                .location(LocationDTO.builder()
                    .city(city)
                    .country(country)
                    .build())
                .period(PeriodDTO.builder()
                    .from(from)
                    .to(to)
                    .build())
                .count(readings.size())
                .readings(readings.stream()
                    .map(AQIReadingDTO::fromDomain)
                    .collect(Collectors.toList()))
                .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private String city;
        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodDTO {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private Instant from;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private Instant to;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherReadingDTO {
        private Double temperature;
        private String temperatureUnit;
        private Double humidity;
        private Double pressure;
        private Double windSpeed;
        private Integer windDirection;
        private String condition;
        private String description;
        private Double visibility;
        private Integer cloudiness;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private Instant timestamp;

        public static WeatherReadingDTO fromDomain(WeatherReading reading) {
            return WeatherReadingDTO.builder()
                .temperature(reading.getTemperatureCelsius())
                .temperatureUnit("celsius")
                .humidity(reading.getHumidity())
                .pressure(reading.getPressure())
                .windSpeed(reading.getWindSpeed())
                .windDirection(reading.getWindDirection())
                .condition(reading.getWeatherCondition())
                .description(reading.getWeatherDescription())
                .visibility(reading.getVisibility())
                .cloudiness(reading.getCloudiness())
                .timestamp(reading.getTimestamp())
                .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AQIReadingDTO {
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

        public static AQIReadingDTO fromDomain(AQIReading reading) {
            return AQIReadingDTO.builder()
                .aqi(reading.getAqi())
                .level(reading.getLevel() != null ? reading.getLevel().name() : null)
                .levelDescription(reading.getLevel() != null ? reading.getLevel().getDescription() : null)
                .pm25(reading.getPm25())
                .pm10(reading.getPm10())
                .co(reading.getCo())
                .no2(reading.getNo2())
                .so2(reading.getSo2())
                .o3(reading.getO3())
                .timestamp(reading.getTimestamp())
                .build();
        }
    }
}
