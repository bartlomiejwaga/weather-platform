package com.weather.adapters.outbound.scraper;

import com.weather.application.port.output.ScraperPort;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.Forecast;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JsoupScraperAdapter implements ScraperPort {

    @Value("${weather.scraper.enabled:true}")
    private boolean enabled;

    @Value("${weather.scraper.timeout:10000}")
    private int timeout;

    @Value("${weather.scraper.user-agent:Mozilla/5.0}")
    private String userAgent;

    @Override
    public Optional<WeatherReading> scrapeWeather(String city, String country) {
        if (!enabled) {
            log.debug("Scraper is disabled");
            return Optional.empty();
        }

        try {
            log.info("Attempting to scrape weather for {}", city);

            String url = String.format("https://www.google.com/search?q=weather+%s", city.replace(" ", "+"));

            Document doc = Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(timeout)
                .get();

            Location location = Location.builder()
                .city(city)
                .country(country)
                .build();

            WeatherReading reading = WeatherReading.builder()
                .location(location)
                .timestamp(Instant.now())
                .dataSource(WeatherReading.DataSource.SCRAPER_FALLBACK)
                .createdAt(Instant.now())
                .build();

            log.warn("Scraper returned incomplete data for {}", city);
            return Optional.of(reading);

        } catch (Exception e) {
            log.error("Error scraping weather for {}: {}", city, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<AQIReading> scrapeAirQuality(String city, String country) {
        if (!enabled) {
            return Optional.empty();
        }

        try {
            log.info("Attempting to scrape AQI for {}", city);

            Location location = Location.builder()
                .city(city)
                .country(country)
                .build();

            AQIReading reading = AQIReading.builder()
                .location(location)
                .timestamp(Instant.now())
                .dataSource(WeatherReading.DataSource.SCRAPER_FALLBACK)
                .createdAt(Instant.now())
                .build();

            log.warn("Scraper returned incomplete AQI data for {}", city);
            return Optional.of(reading);

        } catch (Exception e) {
            log.error("Error scraping AQI for {}: {}", city, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Forecast>> scrapeForecast(String city, String country, int days) {
        if (!enabled) {
            return Optional.empty();
        }

        log.warn("Forecast scraping not fully implemented, returning empty");
        return Optional.empty();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
