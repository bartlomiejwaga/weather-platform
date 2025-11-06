package com.weather.application.usecase;

import com.weather.application.port.input.GetForecastUseCase;
import com.weather.application.port.output.*;
import com.weather.domain.model.Forecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetForecastService implements GetForecastUseCase {

    private final WeatherProviderPort weatherProvider;
    private final ScraperPort scraper;
    private final CachePort cache;
    private final StoragePort storage;

    private static final Duration FORECAST_CACHE_TTL = Duration.ofHours(1);

    @Override
    public List<Forecast> getForecast(String city, String country, int days) {
        if (days < 1 || days > 7) {
            throw new IllegalArgumentException("Days must be between 1 and 7");
        }

        String locationKey = buildLocationKey(city, country);
        String cacheKey = String.format("forecast:%s:%d", locationKey, days);

        log.info("Fetching {}-day forecast for {}", days, locationKey);

        Optional<List<Forecast>> apiResult = weatherProvider.getForecast(city, country, days);
        if (apiResult.isPresent()) {
            List<Forecast> forecasts = apiResult.get();
            forecasts.forEach(storage::saveForecast);
            log.info("Fetched forecast from API for {}", locationKey);
            return forecasts;
        }

        if (scraper.isEnabled()) {
            Optional<List<Forecast>> scraperResult = scraper.scrapeForecast(city, country, days);
            if (scraperResult.isPresent()) {
                List<Forecast> forecasts = scraperResult.get();
                forecasts.forEach(storage::saveForecast);
                log.warn("Fetched forecast from scraper for {}", locationKey);
                return forecasts;
            }
        }

        List<Forecast> stored = storage.getForecasts(locationKey, days);
        if (!stored.isEmpty()) {
            log.warn("Using stored forecast for {}", locationKey);
            return stored;
        }

        log.error("Failed to fetch forecast for {}", locationKey);
        return new ArrayList<>();
    }

    private String buildLocationKey(String city, String country) {
        return country != null
            ? String.format("%s,%s", city, country).toLowerCase()
            : city.toLowerCase();
    }
}
