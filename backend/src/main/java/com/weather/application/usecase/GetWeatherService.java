package com.weather.application.usecase;

import com.weather.application.port.input.GetWeatherUseCase;
import com.weather.application.port.output.*;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetWeatherService implements GetWeatherUseCase {

    private final WeatherProviderPort weatherProvider;
    private final AirQualityProviderPort airQualityProvider;
    private final ScraperPort scraper;
    private final CachePort cache;
    private final StoragePort storage;

    private static final Duration WEATHER_CACHE_TTL = Duration.ofMinutes(10);
    private static final Duration AQI_CACHE_TTL = Duration.ofMinutes(30);

    @Override
    public WeatherResponse getCurrentWeather(String city, String country) {
        String locationKey = buildLocationKey(city, country);
        log.info("Fetching weather for location: {}", locationKey);

        WeatherReading cachedWeather = getCachedWeather(locationKey);
        AQIReading cachedAQI = getCachedAQI(locationKey);

        if (cachedWeather != null && cachedWeather.isRecent() && cachedAQI != null) {
            log.debug("Returning cached data for {}", locationKey);
            return new WeatherResponse(cachedWeather, cachedAQI, true);
        }

        WeatherReading weather = fetchWeather(city, country, locationKey);
        AQIReading aqi = fetchAirQuality(city, country, locationKey);

        if (weather != null) {
            storage.saveWeatherReading(weather);
        }
        if (aqi != null) {
            storage.saveAQIReading(aqi);
        }

        return new WeatherResponse(weather, aqi, false);
    }

    private WeatherReading fetchWeather(String city, String country, String locationKey) {
        Optional<WeatherReading> apiResult = weatherProvider.getCurrentWeather(city, country);
        if (apiResult.isPresent()) {
            WeatherReading reading = apiResult.get();
            reading.setDataSource(WeatherReading.DataSource.OPENWEATHER_API);
            reading.setCreatedAt(Instant.now());
            cacheWeather(locationKey, reading);
            log.info("Fetched weather from API for {}", locationKey);
            return reading;
        }

        if (scraper.isEnabled()) {
            Optional<WeatherReading> scraperResult = scraper.scrapeWeather(city, country);
            if (scraperResult.isPresent()) {
                WeatherReading reading = scraperResult.get();
                reading.setDataSource(WeatherReading.DataSource.SCRAPER_FALLBACK);
                reading.setCreatedAt(Instant.now());
                cacheWeather(locationKey, reading);
                log.warn("Fetched weather from scraper fallback for {}", locationKey);
                return reading;
            }
        }

        Optional<WeatherReading> stored = storage.getLatestWeatherReading(locationKey);
        if (stored.isPresent()) {
            log.warn("Using stored weather data for {}", locationKey);
            return stored.get();
        }

        log.error("Failed to fetch weather data for {}", locationKey);
        return null;
    }

    private AQIReading fetchAirQuality(String city, String country, String locationKey) {
        Optional<AQIReading> apiResult = airQualityProvider.getCurrentAirQuality(city, country);
        if (apiResult.isPresent()) {
            AQIReading reading = apiResult.get();
            reading.setDataSource(WeatherReading.DataSource.IQAIR_API);
            reading.setCreatedAt(Instant.now());
            cacheAQI(locationKey, reading);
            log.info("Fetched AQI from API for {}", locationKey);
            return reading;
        }

        if (scraper.isEnabled()) {
            Optional<AQIReading> scraperResult = scraper.scrapeAirQuality(city, country);
            if (scraperResult.isPresent()) {
                AQIReading reading = scraperResult.get();
                reading.setDataSource(WeatherReading.DataSource.SCRAPER_FALLBACK);
                reading.setCreatedAt(Instant.now());
                cacheAQI(locationKey, reading);
                log.warn("Fetched AQI from scraper fallback for {}", locationKey);
                return reading;
            }
        }

        Optional<AQIReading> stored = storage.getLatestAQIReading(locationKey);
        if (stored.isPresent()) {
            log.warn("Using stored AQI data for {}", locationKey);
            return stored.get();
        }

        log.error("Failed to fetch AQI data for {}", locationKey);
        return null;
    }

    private WeatherReading getCachedWeather(String locationKey) {
        return cache.get("weather:" + locationKey, WeatherReading.class).orElse(null);
    }

    private AQIReading getCachedAQI(String locationKey) {
        return cache.get("aqi:" + locationKey, AQIReading.class).orElse(null);
    }

    private void cacheWeather(String locationKey, WeatherReading reading) {
        cache.put("weather:" + locationKey, reading, WEATHER_CACHE_TTL);
    }

    private void cacheAQI(String locationKey, AQIReading reading) {
        cache.put("aqi:" + locationKey, reading, AQI_CACHE_TTL);
    }

    private String buildLocationKey(String city, String country) {
        return country != null
            ? String.format("%s,%s", city, country).toLowerCase()
            : city.toLowerCase();
    }
}
