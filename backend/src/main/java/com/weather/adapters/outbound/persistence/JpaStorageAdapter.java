package com.weather.adapters.outbound.persistence;

import com.weather.adapters.outbound.persistence.entity.*;
import com.weather.adapters.outbound.persistence.repository.*;
import com.weather.application.port.output.StoragePort;
import com.weather.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaStorageAdapter implements StoragePort {

    private final WeatherReadingRepository weatherRepository;
    private final AQIReadingRepository aqiRepository;
    private final ForecastRepository forecastRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public WeatherReading saveWeatherReading(WeatherReading reading) {
        WeatherReadingEntity entity = WeatherReadingMapper.toEntity(reading);
        WeatherReadingEntity saved = weatherRepository.save(entity);
        return WeatherReadingMapper.toDomain(saved);
    }

    @Override
    public List<WeatherReading> getWeatherHistory(String locationKey, Instant from, Instant to) {
        return weatherRepository.findByLocationKeyAndTimestampBetween(locationKey, from, to)
            .stream()
            .map(WeatherReadingMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<WeatherReading> getLatestWeatherReading(String locationKey) {
        return weatherRepository.findTopByLocationKeyOrderByTimestampDesc(locationKey)
            .map(WeatherReadingMapper::toDomain);
    }

    @Override
    public AQIReading saveAQIReading(AQIReading reading) {
        AQIReadingEntity entity = AQIReadingMapper.toEntity(reading);
        AQIReadingEntity saved = aqiRepository.save(entity);
        return AQIReadingMapper.toDomain(saved);
    }

    @Override
    public List<AQIReading> getAQIHistory(String locationKey, Instant from, Instant to) {
        return aqiRepository.findByLocationKeyAndTimestampBetween(locationKey, from, to)
            .stream()
            .map(AQIReadingMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<AQIReading> getLatestAQIReading(String locationKey) {
        return aqiRepository.findTopByLocationKeyOrderByTimestampDesc(locationKey)
            .map(AQIReadingMapper::toDomain);
    }

    @Override
    public Forecast saveForecast(Forecast forecast) {
        ForecastEntity entity = ForecastMapper.toEntity(forecast);
        ForecastEntity saved = forecastRepository.save(entity);
        return ForecastMapper.toDomain(saved);
    }

    @Override
    public List<Forecast> getForecasts(String locationKey, int days) {
        return forecastRepository.findByLocationKeyOrderByForecastDateAsc(locationKey)
            .stream()
            .limit(days)
            .map(ForecastMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Subscription saveSubscription(Subscription subscription) {
        SubscriptionEntity entity = SubscriptionMapper.toEntity(subscription);
        SubscriptionEntity saved = subscriptionRepository.save(entity);
        return SubscriptionMapper.toDomain(saved);
    }

    @Override
    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }

    @Override
    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
            .map(SubscriptionMapper::toDomain);
    }

    @Override
    public List<Subscription> getSubscriptionsByUserId(String userId) {
        return subscriptionRepository.findByUserId(userId)
            .stream()
            .map(SubscriptionMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Subscription> getActiveSubscriptions() {
        return subscriptionRepository.findByActiveTrue()
            .stream()
            .map(SubscriptionMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Subscription updateLastNotified(Long id, Instant timestamp) {
        SubscriptionEntity entity = subscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        entity.setLastNotifiedAt(timestamp);
        SubscriptionEntity saved = subscriptionRepository.save(entity);
        return SubscriptionMapper.toDomain(saved);
    }
}
