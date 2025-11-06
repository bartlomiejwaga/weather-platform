package com.weather.adapters.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "aqi_readings", indexes = {
    @Index(name = "idx_aqi_location_timestamp", columnList = "locationKey,timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AQIReadingEntity {

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

    private Integer aqi;

    @Enumerated(EnumType.STRING)
    private AQILevelType level;

    private Double pm25;

    private Double pm10;

    private Double co;

    private Double no2;

    private Double so2;

    private Double o3;

    @Enumerated(EnumType.STRING)
    private DataSourceType dataSource;

    @Column(nullable = false)
    private Instant createdAt;

    public enum AQILevelType {
        GOOD,
        MODERATE,
        UNHEALTHY_SENSITIVE,
        UNHEALTHY,
        VERY_UNHEALTHY,
        HAZARDOUS
    }

    public enum DataSourceType {
        OPENWEATHER_API,
        IQAIR_API,
        SCRAPER_FALLBACK,
        CACHED
    }
}
