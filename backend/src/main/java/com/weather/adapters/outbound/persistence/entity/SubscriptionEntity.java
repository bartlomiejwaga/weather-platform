package com.weather.adapters.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "subscriptions", indexes = {
    @Index(name = "idx_subscription_user", columnList = "userId"),
    @Index(name = "idx_subscription_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String city;

    private String country;

    private Double latitude;

    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String alertTypes; // Stored as comma-separated values

    private Double maxTemperature;

    private Double minTemperature;

    private Integer maxAQI;

    private Double maxWindSpeed;

    private Double maxPrecipitation;

    private Integer maxUVIndex;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant lastNotifiedAt;
}
