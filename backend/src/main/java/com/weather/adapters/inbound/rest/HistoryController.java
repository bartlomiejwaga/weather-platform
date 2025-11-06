package com.weather.adapters.inbound.rest;

import com.weather.adapters.inbound.rest.dto.HistoryResponseDTO;
import com.weather.application.port.input.GetHistoryUseCase;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.WeatherReading;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * REST controller for historical weather data
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
@Tag(name = "History", description = "Historical weather data endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HistoryController {

    private final GetHistoryUseCase getHistoryUseCase;

    @GetMapping("/weather")
    @Operation(summary = "Get weather history", description = "Retrieves historical weather data for a location")
    public ResponseEntity<HistoryResponseDTO.WeatherHistoryResponseDTO> getWeatherHistory(
        @Parameter(description = "City name", required = true, example = "London")
        @RequestParam String city,

        @Parameter(description = "Country code (ISO 3166)", example = "GB")
        @RequestParam(required = false) String country,

        @Parameter(description = "Start date (ISO 8601)", required = true, example = "2024-01-01T00:00:00Z")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,

        @Parameter(description = "End date (ISO 8601)", required = true, example = "2024-01-31T23:59:59Z")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        log.info("REST request - Get weather history for city: {}, from: {}, to: {}", city, from, to);

        List<WeatherReading> readings = getHistoryUseCase.getWeatherHistory(city, country, from, to);

        HistoryResponseDTO.WeatherHistoryResponseDTO response = HistoryResponseDTO.WeatherHistoryResponseDTO.fromDomain(
            city, country, from, to, readings
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/aqi")
    @Operation(summary = "Get air quality history", description = "Retrieves historical air quality data for a location")
    public ResponseEntity<HistoryResponseDTO.AQIHistoryResponseDTO> getAQIHistory(
        @Parameter(description = "City name", required = true, example = "London")
        @RequestParam String city,

        @Parameter(description = "Country code (ISO 3166)", example = "GB")
        @RequestParam(required = false) String country,

        @Parameter(description = "Start date (ISO 8601)", required = true, example = "2024-01-01T00:00:00Z")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,

        @Parameter(description = "End date (ISO 8601)", required = true, example = "2024-01-31T23:59:59Z")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        log.info("REST request - Get AQI history for city: {}, from: {}, to: {}", city, from, to);

        List<AQIReading> readings = getHistoryUseCase.getAQIHistory(city, country, from, to);

        HistoryResponseDTO.AQIHistoryResponseDTO response = HistoryResponseDTO.AQIHistoryResponseDTO.fromDomain(
            city, country, from, to, readings
        );

        return ResponseEntity.ok(response);
    }
}
