package com.weather.adapters.inbound.rest;

import com.weather.application.port.input.GetHistoryUseCase;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for HistoryController
 */
@DisplayName("History Controller Integration Tests")
class HistoryControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private GetHistoryUseCase getHistoryUseCase;

    @Test
    @DisplayName("Should get weather history")
    void shouldGetWeatherHistory() throws Exception {
        // Given
        String city = "London";
        String country = "GB";
        Instant from = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant to = Instant.now();

        Location location = Location.builder()
            .city(city)
            .country(country)
            .latitude(51.5074)
            .longitude(-0.1278)
            .build();

        List<WeatherReading> readings = Arrays.asList(
            createWeatherReading(location, 15.5, 65.0, from.plus(1, ChronoUnit.DAYS)),
            createWeatherReading(location, 16.0, 70.0, from.plus(2, ChronoUnit.DAYS)),
            createWeatherReading(location, 14.5, 68.0, from.plus(3, ChronoUnit.DAYS)),
            createWeatherReading(location, 17.0, 62.0, from.plus(4, ChronoUnit.DAYS)),
            createWeatherReading(location, 15.0, 66.0, from.plus(5, ChronoUnit.DAYS))
        );

        when(getHistoryUseCase.getWeatherHistory(eq(city), eq(country), any(Instant.class), any(Instant.class)))
            .thenReturn(readings);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/weather")
                .param("city", city)
                .param("country", country)
                .param("from", from.toString())
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.location.city", is(city)))
            .andExpect(jsonPath("$.location.country", is(country)))
            .andExpect(jsonPath("$.period.from", notNullValue()))
            .andExpect(jsonPath("$.period.to", notNullValue()))
            .andExpect(jsonPath("$.count", is(5)))
            .andExpect(jsonPath("$.readings", hasSize(5)))
            .andExpect(jsonPath("$.readings[0].temperature", is(15.5)))
            .andExpect(jsonPath("$.readings[0].temperatureUnit", is("celsius")))
            .andExpect(jsonPath("$.readings[0].humidity", is(65.0)))
            .andExpect(jsonPath("$.readings[0].pressure", is(1013.0)))
            .andExpect(jsonPath("$.readings[0].windSpeed", is(5.5)))
            .andExpect(jsonPath("$.readings[0].windDirection", is(180)))
            .andExpect(jsonPath("$.readings[0].condition", is("Clouds")))
            .andExpect(jsonPath("$.readings[0].description", is("Partly cloudy")))
            .andExpect(jsonPath("$.readings[0].visibility", is(10000.0)))
            .andExpect(jsonPath("$.readings[0].cloudiness", is(50)))
            .andExpect(jsonPath("$.readings[0].timestamp", notNullValue()))
            .andExpect(jsonPath("$.readings[1].temperature", is(16.0)))
            .andExpect(jsonPath("$.readings[1].humidity", is(70.0)));

        verify(getHistoryUseCase).getWeatherHistory(eq(city), eq(country), any(Instant.class), any(Instant.class));
    }

    @Test
    @DisplayName("Should get weather history without country")
    void shouldGetWeatherHistoryWithoutCountry() throws Exception {
        // Given
        String city = "Paris";
        Instant from = Instant.now().minus(3, ChronoUnit.DAYS);
        Instant to = Instant.now();

        Location location = Location.builder()
            .city(city)
            .build();

        List<WeatherReading> readings = Arrays.asList(
            createWeatherReading(location, 18.0, 60.0, from.plus(1, ChronoUnit.DAYS)),
            createWeatherReading(location, 19.0, 62.0, from.plus(2, ChronoUnit.DAYS))
        );

        when(getHistoryUseCase.getWeatherHistory(eq(city), any(), any(Instant.class), any(Instant.class)))
            .thenReturn(readings);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/weather")
                .param("city", city)
                .param("from", from.toString())
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.location.city", is(city)))
            .andExpect(jsonPath("$.count", is(2)))
            .andExpect(jsonPath("$.readings", hasSize(2)));

        verify(getHistoryUseCase).getWeatherHistory(eq(city), any(), any(Instant.class), any(Instant.class));
    }

    @Test
    @DisplayName("Should get AQI history")
    void shouldGetAQIHistory() throws Exception {
        // Given
        String city = "Beijing";
        String country = "CN";
        Instant from = Instant.now().minus(5, ChronoUnit.DAYS);
        Instant to = Instant.now();

        Location location = Location.builder()
            .city(city)
            .country(country)
            .build();

        List<AQIReading> readings = Arrays.asList(
            createAQIReading(location, 150, AQIReading.AQILevel.UNHEALTHY, 85.5, from.plus(1, ChronoUnit.DAYS)),
            createAQIReading(location, 120, AQIReading.AQILevel.UNHEALTHY_SENSITIVE, 65.0, from.plus(2, ChronoUnit.DAYS)),
            createAQIReading(location, 95, AQIReading.AQILevel.MODERATE, 45.5, from.plus(3, ChronoUnit.DAYS)),
            createAQIReading(location, 75, AQIReading.AQILevel.MODERATE, 35.0, from.plus(4, ChronoUnit.DAYS))
        );

        when(getHistoryUseCase.getAQIHistory(eq(city), eq(country), any(Instant.class), any(Instant.class)))
            .thenReturn(readings);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/aqi")
                .param("city", city)
                .param("country", country)
                .param("from", from.toString())
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.location.city", is(city)))
            .andExpect(jsonPath("$.location.country", is(country)))
            .andExpect(jsonPath("$.count", is(4)))
            .andExpect(jsonPath("$.readings", hasSize(4)))
            .andExpect(jsonPath("$.readings[0].aqi", is(150)))
            .andExpect(jsonPath("$.readings[0].level", is("UNHEALTHY")))
            .andExpect(jsonPath("$.readings[0].levelDescription", is("Unhealthy")))
            .andExpect(jsonPath("$.readings[0].pm25", is(85.5)))
            .andExpect(jsonPath("$.readings[0].pm10", is(120.0)))
            .andExpect(jsonPath("$.readings[0].co", is(1.5)))
            .andExpect(jsonPath("$.readings[0].no2", is(45.0)))
            .andExpect(jsonPath("$.readings[0].so2", is(20.0)))
            .andExpect(jsonPath("$.readings[0].o3", is(80.0)))
            .andExpect(jsonPath("$.readings[0].timestamp", notNullValue()))
            .andExpect(jsonPath("$.readings[1].aqi", is(120)))
            .andExpect(jsonPath("$.readings[1].level", is("UNHEALTHY_SENSITIVE")))
            .andExpect(jsonPath("$.readings[2].aqi", is(95)))
            .andExpect(jsonPath("$.readings[2].level", is("MODERATE")));

        verify(getHistoryUseCase).getAQIHistory(eq(city), eq(country), any(Instant.class), any(Instant.class));
    }

    @Test
    @DisplayName("Should get AQI history without country")
    void shouldGetAQIHistoryWithoutCountry() throws Exception {
        // Given
        String city = "Delhi";
        Instant from = Instant.now().minus(2, ChronoUnit.DAYS);
        Instant to = Instant.now();

        Location location = Location.builder()
            .city(city)
            .build();

        List<AQIReading> readings = Arrays.asList(
            createAQIReading(location, 180, AQIReading.AQILevel.UNHEALTHY, 95.0, from.plus(1, ChronoUnit.DAYS))
        );

        when(getHistoryUseCase.getAQIHistory(eq(city), any(), any(Instant.class), any(Instant.class)))
            .thenReturn(readings);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/aqi")
                .param("city", city)
                .param("from", from.toString())
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.location.city", is(city)))
            .andExpect(jsonPath("$.count", is(1)))
            .andExpect(jsonPath("$.readings", hasSize(1)));

        verify(getHistoryUseCase).getAQIHistory(eq(city), any(), any(Instant.class), any(Instant.class));
    }

    @Test
    @DisplayName("Should handle empty weather history")
    void shouldHandleEmptyWeatherHistory() throws Exception {
        // Given
        String city = "Unknown";
        Instant from = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant to = Instant.now();

        when(getHistoryUseCase.getWeatherHistory(eq(city), any(), any(Instant.class), any(Instant.class)))
            .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/weather")
                .param("city", city)
                .param("from", from.toString())
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count", is(0)))
            .andExpect(jsonPath("$.readings", hasSize(0)));
    }

    @Test
    @DisplayName("Should handle empty AQI history")
    void shouldHandleEmptyAQIHistory() throws Exception {
        // Given
        String city = "Unknown";
        Instant from = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant to = Instant.now();

        when(getHistoryUseCase.getAQIHistory(eq(city), any(), any(Instant.class), any(Instant.class)))
            .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/aqi")
                .param("city", city)
                .param("from", from.toString())
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count", is(0)))
            .andExpect(jsonPath("$.readings", hasSize(0)));
    }

    @Test
    @DisplayName("Should require city parameter for weather history")
    void shouldRequireCityParameterForWeatherHistory() throws Exception {
        // Given
        Instant from = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant to = Instant.now();

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/weather")
                .param("from", from.toString())
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should require from parameter for weather history")
    void shouldRequireFromParameterForWeatherHistory() throws Exception {
        // Given
        Instant to = Instant.now();

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/weather")
                .param("city", "London")
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should require to parameter for weather history")
    void shouldRequireToParameterForWeatherHistory() throws Exception {
        // Given
        Instant from = Instant.now().minus(1, ChronoUnit.DAYS);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/weather")
                .param("city", "London")
                .param("from", from.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should require city parameter for AQI history")
    void shouldRequireCityParameterForAQIHistory() throws Exception {
        // Given
        Instant from = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant to = Instant.now();

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/aqi")
                .param("from", from.toString())
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // Helper methods
    private WeatherReading createWeatherReading(Location location, Double temp, Double humidity, Instant timestamp) {
        return WeatherReading.builder()
            .location(location)
            .temperatureCelsius(temp)
            .humidity(humidity)
            .pressure(1013.0)
            .windSpeed(5.5)
            .windDirection(180)
            .weatherCondition("Clouds")
            .weatherDescription("Partly cloudy")
            .visibility(10000.0)
            .cloudiness(50)
            .timestamp(timestamp)
            .dataSource(WeatherReading.DataSource.OPENWEATHER_API)
            .build();
    }

    private AQIReading createAQIReading(Location location, Integer aqi, AQIReading.AQILevel level,
                                        Double pm25, Instant timestamp) {
        return AQIReading.builder()
            .location(location)
            .aqi(aqi)
            .level(level)
            .pm25(pm25)
            .pm10(120.0)
            .co(1.5)
            .no2(45.0)
            .so2(20.0)
            .o3(80.0)
            .timestamp(timestamp)
            .dataSource(WeatherReading.DataSource.IQAIR_API)
            .build();
    }
}
