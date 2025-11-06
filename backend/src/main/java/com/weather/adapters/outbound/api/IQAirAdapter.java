package com.weather.adapters.outbound.api;

import com.weather.application.port.output.AirQualityProviderPort;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.Location;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class IQAirAdapter implements AirQualityProviderPort {

    private final WebClient.Builder webClientBuilder;

    @Value("${weather.iqair.api-key:}")
    private String apiKey;

    @Value("${weather.iqair.base-url:https://api.airvisual.com/v2}")
    private String baseUrl;

    @Value("${weather.iqair.timeout:5000}")
    private int timeout;

    @Override
    @CircuitBreaker(name = "weatherApi", fallbackMethod = "getCurrentAirQualityFallback")
    @Retry(name = "weatherApi")
    public Optional<AQIReading> getCurrentAirQuality(String city, String country) {
        if (apiKey == null || apiKey.isBlank()) {
            log.debug("IQAir API key not configured, skipping");
            return Optional.empty();
        }

        try {
            WebClient client = webClientBuilder.baseUrl(baseUrl).build();

            IQAirResponse response = client.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/city")
                    .queryParam("city", city)
                    .queryParam("state", "")
                    .queryParam("country", country != null ? country : "")
                    .queryParam("key", apiKey)
                    .build())
                .retrieve()
                .bodyToMono(IQAirResponse.class)
                .timeout(Duration.ofMillis(timeout))
                .block();

            if (response == null || response.data() == null) {
                log.warn("Empty response from IQAir API for {}", city);
                return Optional.empty();
            }

            AQIReading reading = mapToAQIReading(response, city, country);
            log.info("Successfully fetched AQI from IQAir for {}", city);
            return Optional.of(reading);

        } catch (Exception e) {
            log.error("Error fetching AQI from IQAir for {}: {}", city, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public String getProviderName() {
        return "IQAir";
    }

    private AQIReading mapToAQIReading(IQAirResponse response, String city, String country) {
        IQAirResponse.Data data = response.data();
        IQAirResponse.Current current = data.current();
        IQAirResponse.Pollution pollution = current.pollution();

        Location location = Location.builder()
            .city(city)
            .country(country)
            .build();

        return AQIReading.builder()
            .location(location)
            .timestamp(Instant.parse(pollution.ts()))
            .aqi(pollution.aqius())
            .pm25(pollution.p2() != null ? pollution.p2().conc() : null)
            .pm10(pollution.p1() != null ? pollution.p1().conc() : null)
            .build();
    }

    private Optional<AQIReading> getCurrentAirQualityFallback(String city, String country, Exception e) {
        log.error("Circuit breaker activated for IQAir getCurrentAirQuality: {}", e.getMessage());
        return Optional.empty();
    }

    record IQAirResponse(
        String status,
        Data data
    ) {
        record Data(
            String city,
            String state,
            String country,
            Location location,
            Current current
        ) {}

        record Location(
            String type,
            Double[] coordinates
        ) {}

        record Current(
            Pollution pollution,
            Weather weather
        ) {}

        record Pollution(
            String ts,
            Integer aqius,
            String mainus,
            Integer aqicn,
            String maincn,
            Pollutant p2,
            Pollutant p1
        ) {}

        record Pollutant(
            Double conc,
            Integer aqius,
            Integer aqicn
        ) {}

        record Weather(
            String ts,
            Double tp,
            Double pr,
            Integer hu,
            Double ws,
            Integer wd,
            String ic
        ) {}
    }
}
