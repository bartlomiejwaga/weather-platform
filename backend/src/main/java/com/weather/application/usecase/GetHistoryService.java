package com.weather.application.usecase;

import com.weather.application.port.input.GetHistoryUseCase;
import com.weather.application.port.output.StoragePort;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.WeatherReading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetHistoryService implements GetHistoryUseCase {

    private final StoragePort storage;

    @Override
    public List<WeatherReading> getWeatherHistory(String city, String country, Instant from, Instant to) {
        String locationKey = buildLocationKey(city, country);
        log.info("Fetching weather history for {} from {} to {}", locationKey, from, to);
        return storage.getWeatherHistory(locationKey, from, to);
    }

    @Override
    public List<AQIReading> getAQIHistory(String city, String country, Instant from, Instant to) {
        String locationKey = buildLocationKey(city, country);
        log.info("Fetching AQI history for {} from {} to {}", locationKey, from, to);
        return storage.getAQIHistory(locationKey, from, to);
    }

    private String buildLocationKey(String city, String country) {
        return country != null
            ? String.format("%s,%s", city, country).toLowerCase()
            : city.toLowerCase();
    }
}
