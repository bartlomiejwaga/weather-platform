package com.weather.adapters.inbound.rest;

import com.weather.adapters.inbound.rest.dto.ForecastResponseDTO;
import com.weather.application.port.input.GetForecastUseCase;
import com.weather.domain.model.Forecast;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for weather forecast operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/forecast")
@RequiredArgsConstructor
@Tag(name = "Forecast", description = "Weather forecast endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ForecastController {

    private final GetForecastUseCase getForecastUseCase;

    @GetMapping
    @Operation(summary = "Get weather forecast", description = "Retrieves weather forecast for specified number of days")
    public ResponseEntity<List<ForecastResponseDTO>> getForecast(
        @Parameter(description = "City name", required = true, example = "London")
        @RequestParam String city,

        @Parameter(description = "Country code (ISO 3166)", example = "GB")
        @RequestParam(required = false) String country,

        @Parameter(description = "Number of days (1-7)", example = "5")
        @RequestParam(defaultValue = "5") @Min(1) @Max(7) int days
    ) {
        log.info("REST request - Get forecast for city: {}, country: {}, days: {}", city, country, days);

        List<Forecast> forecasts = getForecastUseCase.getForecast(city, country, days);

        List<ForecastResponseDTO> response = forecasts.stream()
            .map(ForecastResponseDTO::fromDomain)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{city}")
    @Operation(summary = "Get forecast by path", description = "Alternative endpoint using path parameter")
    public ResponseEntity<List<ForecastResponseDTO>> getForecastByPath(
        @Parameter(description = "City name", required = true, example = "Paris")
        @PathVariable String city,

        @Parameter(description = "Country code", example = "FR")
        @RequestParam(required = false) String country,

        @Parameter(description = "Number of days (1-7)", example = "5")
        @RequestParam(defaultValue = "5") @Min(1) @Max(7) int days
    ) {
        log.info("REST request - Get forecast (path) for city: {}, days: {}", city, days);

        List<Forecast> forecasts = getForecastUseCase.getForecast(city, country, days);

        List<ForecastResponseDTO> response = forecasts.stream()
            .map(ForecastResponseDTO::fromDomain)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
