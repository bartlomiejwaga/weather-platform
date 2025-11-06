package com.weather.adapters.outbound.persistence.repository;

import com.weather.adapters.outbound.persistence.entity.AQIReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AQIReadingRepository extends JpaRepository<AQIReadingEntity, Long> {

    List<AQIReadingEntity> findByLocationKeyAndTimestampBetween(
        String locationKey,
        Instant from,
        Instant to
    );

    Optional<AQIReadingEntity> findTopByLocationKeyOrderByTimestampDesc(String locationKey);

    List<AQIReadingEntity> findByLocationKeyOrderByTimestampDesc(String locationKey);
}
