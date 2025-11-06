package com.weather.adapters.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "weather_readings", indexes = {
    @Index(name = "idx_location_timestamp", columnList = "locationKey,timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherReadingEntity {

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
    private Instant timestamp;

    private Double temperatureCelsius;

    private Double temperatureFahrenheit;

    private Double humidity;

    private Double pressure;

    private Double windSpeed;

    private Integer windDirection;

    private String weatherCondition;

    private String weatherDescription;

    private String weatherIcon;

    private Double visibility;

    private Integer cloudiness;

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
