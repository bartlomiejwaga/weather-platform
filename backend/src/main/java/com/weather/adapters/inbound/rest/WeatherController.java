package com.weather.adapters.inbound.rest;

import com.weather.adapters.inbound.rest.dto.WeatherResponseDTO;
import com.weather.application.port.input.GetWeatherUseCase;
import com.weather.application.port.input.GetWeatherUseCase.WeatherResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for weather operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Tag(name = "Weather", description = "Weather and air quality data endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WeatherController {

    private final GetWeatherUseCase getWeatherUseCase;

    @GetMapping
    @Operation(summary = "Get current weather", description = "Retrieves current weather and air quality data for a city")
    public ResponseEntity<WeatherResponseDTO> getCurrentWeather(
        @Parameter(description = "City name", required = true, example = "London")
        @RequestParam String city,

        @Parameter(description = "Country code (ISO 3166)", example = "GB")
        @RequestParam(required = false) String country
    ) {
        log.info("REST request - Get current weather for city: {}, country: {}", city, country);

        WeatherResponse response = getWeatherUseCase.getCurrentWeather(city, country);

        WeatherResponseDTO dto = WeatherResponseDTO.fromDomain(response);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/current/{city}")
    @Operation(summary = "Get current weather by path", description = "Alternative endpoint using path parameter")
    public ResponseEntity<WeatherResponseDTO> getCurrentWeatherByPath(
        @Parameter(description = "City name", required = true, example = "Paris")
        @PathVariable String city,

        @Parameter(description = "Country code", example = "FR")
        @RequestParam(required = false) String country
    ) {
        log.info("REST request - Get current weather (path) for city: {}", city);

        WeatherResponse response = getWeatherUseCase.getCurrentWeather(city, country);

        WeatherResponseDTO dto = WeatherResponseDTO.fromDomain(response);

        return ResponseEntity.ok(dto);
    }
}
