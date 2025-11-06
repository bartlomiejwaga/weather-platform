package com.weather.adapters.inbound.rest;

import com.weather.application.port.input.GetForecastUseCase;
import com.weather.domain.model.Forecast;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.LocalDate;
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
 * Integration tests for ForecastController
 */
@DisplayName("Forecast Controller Integration Tests")
class ForecastControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private GetForecastUseCase getForecastUseCase;

    @Test
    @DisplayName("Should get forecast with query parameters")
    void shouldGetForecastWithQueryParams() throws Exception {
        // Given
        Location location = Location.builder()
            .city("London")
            .country("GB")
            .latitude(51.5074)
            .longitude(-0.1278)
            .build();

        List<Forecast> forecasts = Arrays.asList(
            createForecast(1L, location, LocalDate.now().plusDays(1), 10.0, 18.0, 14.0, "Clear", "Clear sky", "01d", 30.0, 5.0),
            createForecast(2L, location, LocalDate.now().plusDays(2), 12.0, 20.0, 16.0, "Clouds", "Few clouds", "02d", 20.0, 4.5),
            createForecast(3L, location, LocalDate.now().plusDays(3), 11.0, 19.0, 15.0, "Rain", "Light rain", "10d", 60.0, 6.0),
            createForecast(4L, location, LocalDate.now().plusDays(4), 13.0, 21.0, 17.0, "Clear", "Clear sky", "01d", 10.0, 3.5),
            createForecast(5L, location, LocalDate.now().plusDays(5), 14.0, 22.0, 18.0, "Clouds", "Broken clouds", "04d", 40.0, 5.5)
        );

        when(getForecastUseCase.getForecast(eq("London"), eq("GB"), eq(5)))
            .thenReturn(forecasts);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast")
                .param("city", "London")
                .param("country", "GB")
                .param("days", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].location.city", is("London")))
            .andExpect(jsonPath("$[0].location.country", is("GB")))
            .andExpect(jsonPath("$[0].temperature.min", is(10.0)))
            .andExpect(jsonPath("$[0].temperature.max", is(18.0)))
            .andExpect(jsonPath("$[0].temperature.avg", is(14.0)))
            .andExpect(jsonPath("$[0].temperature.unit", is("celsius")))
            .andExpect(jsonPath("$[0].weatherCondition.condition", is("Clear")))
            .andExpect(jsonPath("$[0].weatherCondition.description", is("Clear sky")))
            .andExpect(jsonPath("$[0].weatherCondition.icon", is("01d")))
            .andExpect(jsonPath("$[0].precipitation.probability", is(30.0)))
            .andExpect(jsonPath("$[0].precipitation.likelyToRain", is(false)))
            .andExpect(jsonPath("$[0].wind.speed", is(5.0)))
            .andExpect(jsonPath("$[0].wind.unit", is("m/s")))
            .andExpect(jsonPath("$[2].precipitation.probability", is(60.0)))
            .andExpect(jsonPath("$[2].precipitation.likelyToRain", is(true)))
            .andExpect(jsonPath("$[2].weatherCondition.condition", is("Rain")));

        verify(getForecastUseCase).getForecast("London", "GB", 5);
    }

    @Test
    @DisplayName("Should get forecast with default days parameter")
    void shouldGetForecastWithDefaultDays() throws Exception {
        // Given
        Location location = Location.builder()
            .city("Paris")
            .country("FR")
            .build();

        List<Forecast> forecasts = createForecastList(location, 5);

        when(getForecastUseCase.getForecast(eq("Paris"), eq("FR"), eq(5)))
            .thenReturn(forecasts);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast")
                .param("city", "Paris")
                .param("country", "FR")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].location.city", is("Paris")));

        verify(getForecastUseCase).getForecast("Paris", "FR", 5);
    }

    @Test
    @DisplayName("Should get forecast without country parameter")
    void shouldGetForecastWithoutCountry() throws Exception {
        // Given
        Location location = Location.builder()
            .city("Berlin")
            .build();

        List<Forecast> forecasts = createForecastList(location, 3);

        when(getForecastUseCase.getForecast(eq("Berlin"), any(), eq(3)))
            .thenReturn(forecasts);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast")
                .param("city", "Berlin")
                .param("days", "3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].location.city", is("Berlin")));

        verify(getForecastUseCase).getForecast(eq("Berlin"), any(), eq(3));
    }

    @Test
    @DisplayName("Should get forecast by path parameter")
    void shouldGetForecastByPathParameter() throws Exception {
        // Given
        Location location = Location.builder()
            .city("Tokyo")
            .country("JP")
            .build();

        List<Forecast> forecasts = createForecastList(location, 7);

        when(getForecastUseCase.getForecast(eq("Tokyo"), eq("JP"), eq(7)))
            .thenReturn(forecasts);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast/Tokyo")
                .param("country", "JP")
                .param("days", "7")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(7)))
            .andExpect(jsonPath("$[0].location.city", is("Tokyo")))
            .andExpect(jsonPath("$[0].location.country", is("JP")));

        verify(getForecastUseCase).getForecast("Tokyo", "JP", 7);
    }

    @Test
    @DisplayName("Should validate days parameter minimum value")
    void shouldValidateDaysMinimum() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast")
                .param("city", "London")
                .param("days", "0")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate days parameter maximum value")
    void shouldValidateDaysMaximum() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast")
                .param("city", "London")
                .param("days", "8")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle empty forecast list")
    void shouldHandleEmptyForecastList() throws Exception {
        // Given
        when(getForecastUseCase.getForecast(eq("Unknown"), any(), eq(5)))
            .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast")
                .param("city", "Unknown")
                .param("days", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should handle missing city parameter")
    void shouldHandleMissingCityParameter() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast")
                .param("days", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should include complete forecast details")
    void shouldIncludeCompleteForecastDetails() throws Exception {
        // Given
        Location location = Location.builder()
            .city("Madrid")
            .country("ES")
            .latitude(40.4168)
            .longitude(-3.7038)
            .build();

        Instant now = Instant.now();
        Forecast forecast = Forecast.builder()
            .id(1L)
            .location(location)
            .date(LocalDate.now().plusDays(1))
            .tempMin(15.0)
            .tempMax(25.0)
            .tempAvg(20.0)
            .humidity(60)
            .windSpeed(8.5)
            .weatherCondition("Clear")
            .weatherDescription("Clear sky")
            .weatherIcon("01d")
            .precipitationProbability(20.0)
            .precipitationAmount(0.0)
            .cloudiness(10)
            .uvIndex(8)
            .sunrise(now.minusSeconds(3600))
            .sunset(now.plusSeconds(3600))
            .dataSource(WeatherReading.DataSource.OPENWEATHER_API)
            .createdAt(now)
            .build();

        when(getForecastUseCase.getForecast(eq("Madrid"), eq("ES"), eq(1)))
            .thenReturn(Collections.singletonList(forecast));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast")
                .param("city", "Madrid")
                .param("country", "ES")
                .param("days", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].location.latitude", is(40.4168)))
            .andExpect(jsonPath("$[0].location.longitude", is(-3.7038)))
            .andExpect(jsonPath("$[0].humidity", is(60)))
            .andExpect(jsonPath("$[0].cloudiness", is(10)))
            .andExpect(jsonPath("$[0].uvIndex", is(8)))
            .andExpect(jsonPath("$[0].precipitation.amount", is(0.0)))
            .andExpect(jsonPath("$[0].sun.sunrise", notNullValue()))
            .andExpect(jsonPath("$[0].sun.sunset", notNullValue()))
            .andExpect(jsonPath("$[0].dataSource", is("OPENWEATHER_API")))
            .andExpect(jsonPath("$[0].createdAt", notNullValue()));
    }

    // Helper methods
    private Forecast createForecast(Long id, Location location, LocalDate date,
                                     Double tempMin, Double tempMax, Double tempAvg,
                                     String condition, String description, String icon,
                                     Double precipProb, Double windSpeed) {
        return Forecast.builder()
            .id(id)
            .location(location)
            .date(date)
            .tempMin(tempMin)
            .tempMax(tempMax)
            .tempAvg(tempAvg)
            .weatherCondition(condition)
            .weatherDescription(description)
            .weatherIcon(icon)
            .precipitationProbability(precipProb)
            .windSpeed(windSpeed)
            .humidity(65)
            .cloudiness(30)
            .uvIndex(5)
            .dataSource(WeatherReading.DataSource.OPENWEATHER_API)
            .createdAt(Instant.now())
            .build();
    }

    private List<Forecast> createForecastList(Location location, int count) {
        return Arrays.asList(
            createForecast(1L, location, LocalDate.now().plusDays(1), 10.0, 18.0, 14.0, "Clear", "Clear sky", "01d", 20.0, 5.0),
            createForecast(2L, location, LocalDate.now().plusDays(2), 12.0, 20.0, 16.0, "Clouds", "Few clouds", "02d", 30.0, 4.5),
            createForecast(3L, location, LocalDate.now().plusDays(3), 11.0, 19.0, 15.0, "Rain", "Light rain", "10d", 70.0, 6.0),
            createForecast(4L, location, LocalDate.now().plusDays(4), 13.0, 21.0, 17.0, "Clear", "Clear sky", "01d", 10.0, 3.5),
            createForecast(5L, location, LocalDate.now().plusDays(5), 14.0, 22.0, 18.0, "Clouds", "Broken clouds", "04d", 40.0, 5.5),
            createForecast(6L, location, LocalDate.now().plusDays(6), 15.0, 23.0, 19.0, "Clear", "Clear sky", "01d", 15.0, 4.0),
            createForecast(7L, location, LocalDate.now().plusDays(7), 13.0, 21.0, 17.0, "Clouds", "Scattered clouds", "03d", 35.0, 5.0)
        ).subList(0, Math.min(count, 7));
    }
}
