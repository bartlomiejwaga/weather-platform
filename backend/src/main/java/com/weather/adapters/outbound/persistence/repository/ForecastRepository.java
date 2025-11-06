package com.weather.adapters.outbound.persistence.repository;

import com.weather.adapters.outbound.persistence.entity.ForecastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ForecastRepository extends JpaRepository<ForecastEntity, Long> {

    List<ForecastEntity> findByLocationKeyOrderByForecastDateAsc(String locationKey);

    List<ForecastEntity> findByLocationKeyAndForecastDateBetween(
        String locationKey,
        LocalDate from,
        LocalDate to
    );

    void deleteByLocationKeyAndForecastDateBefore(String locationKey, LocalDate date);
}
