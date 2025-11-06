package com.weather.application.port.output;

import com.weather.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface StoragePort {

    WeatherReading saveWeatherReading(WeatherReading reading);
    List<WeatherReading> getWeatherHistory(String locationKey, Instant from, Instant to);
    Optional<WeatherReading> getLatestWeatherReading(String locationKey);

    AQIReading saveAQIReading(AQIReading reading);
    List<AQIReading> getAQIHistory(String locationKey, Instant from, Instant to);
    Optional<AQIReading> getLatestAQIReading(String locationKey);

    Forecast saveForecast(Forecast forecast);
    List<Forecast> getForecasts(String locationKey, int days);

    Subscription saveSubscription(Subscription subscription);
    void deleteSubscription(Long id);
    Optional<Subscription> getSubscriptionById(Long id);
    List<Subscription> getSubscriptionsByUserId(String userId);
    List<Subscription> getActiveSubscriptions();
    Subscription updateLastNotified(Long id, Instant timestamp);
}
