package com.weather.adapters.outbound.persistence.repository;

import com.weather.adapters.outbound.persistence.entity.WeatherReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherReadingRepository extends JpaRepository<WeatherReadingEntity, Long> {

    List<WeatherReadingEntity> findByLocationKeyAndTimestampBetween(
        String locationKey,
        Instant from,
        Instant to
    );

    Optional<WeatherReadingEntity> findTopByLocationKeyOrderByTimestampDesc(String locationKey);

    List<WeatherReadingEntity> findByLocationKeyOrderByTimestampDesc(String locationKey);
}
