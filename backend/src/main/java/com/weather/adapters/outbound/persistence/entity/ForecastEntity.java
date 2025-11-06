package com.weather.adapters.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "forecasts", indexes = {
    @Index(name = "idx_forecast_location_date", columnList = "locationKey,forecastDate")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String locationKey;

    @Column(nullable = false)
    private String city;

    private String country;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private LocalDate forecastDate;

    private Double tempMin;

    private Double tempMax;

    private Double tempAvg;

    private Integer humidity;

    private Double windSpeed;

    private String weatherCondition;

    private String weatherDescription;

    private String weatherIcon;

    private Double precipitationProbability;

    private Double precipitationAmount;

    private Integer cloudiness;

    private Integer uvIndex;

    private Instant sunrise;

    private Instant sunset;

    @Enumerated(EnumType.STRING)
    private DataSourceType dataSource;

    @Column(nullable = false)
    private Instant createdAt;

    public enum DataSourceType {
        OPENWEATHER_API,
        IQAIR_API,
        SCRAPER_FALLBACK,
        CACHED
    }
}
