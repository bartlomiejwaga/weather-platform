package com.weather.adapters.inbound.rest;

import com.weather.application.port.input.GetWeatherUseCase;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WeatherController
 */
@DisplayName("Weather Controller Integration Tests")
class WeatherControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private GetWeatherUseCase getWeatherUseCase;

    @Test
    @DisplayName("Should get current weather with query parameters")
    void shouldGetCurrentWeatherWithQueryParams() throws Exception {
        // Given
        Location location = Location.builder()
            .city("London")
            .country("GB")
            .latitude(51.5074)
            .longitude(-0.1278)
            .build();

        WeatherReading weatherReading = WeatherReading.builder()
            .location(location)
            .temperatureCelsius(15.5)
            .humidity(65.0)
            .pressure(1013.0)
            .windSpeed(5.5)
            .windDirection(180)
            .weatherCondition("Clouds")
            .weatherDescription("Partly cloudy")
            .weatherIcon("03d")
            .visibility(10000.0)
            .cloudiness(50)
            .timestamp(Instant.now())
            .dataSource(WeatherReading.DataSource.OPENWEATHER_API)
            .build();

        AQIReading aqiReading = AQIReading.builder()
            .location(location)
            .aqi(42)
            .level(AQIReading.AQILevel.GOOD)
            .pm25(12.5)
            .pm10(18.3)
            .co(0.5)
            .no2(15.2)
            .so2(5.1)
            .o3(45.3)
            .timestamp(Instant.now())
            .dataSource(WeatherReading.DataSource.IQAIR_API)
            .build();

        GetWeatherUseCase.WeatherResponse response = new GetWeatherUseCase.WeatherResponse(
            weatherReading, aqiReading, false
        );

        when(getWeatherUseCase.getCurrentWeather(eq("London"), eq("GB")))
            .thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/weather")
                .param("city", "London")
                .param("country", "GB")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.location.city", is("London")))
            .andExpect(jsonPath("$.location.country", is("GB")))
            .andExpect(jsonPath("$.location.latitude", is(51.5074)))
            .andExpect(jsonPath("$.location.longitude", is(-0.1278)))
            .andExpect(jsonPath("$.weather.temperature", is(15.5)))
            .andExpect(jsonPath("$.weather.temperatureUnit", is("celsius")))
            .andExpect(jsonPath("$.weather.humidity", is(65.0)))
            .andExpect(jsonPath("$.weather.pressure", is(1013.0)))
            .andExpect(jsonPath("$.weather.windSpeed", is(5.5)))
            .andExpect(jsonPath("$.weather.windDirection", is(180)))
            .andExpect(jsonPath("$.weather.condition", is("Clouds")))
            .andExpect(jsonPath("$.weather.description", is("Partly cloudy")))
            .andExpect(jsonPath("$.weather.icon", is("03d")))
            .andExpect(jsonPath("$.weather.visibility", is(10000.0)))
            .andExpect(jsonPath("$.weather.cloudiness", is(50)))
            .andExpect(jsonPath("$.weather.timestamp", notNullValue()))
            .andExpect(jsonPath("$.airQuality.aqi", is(42)))
            .andExpect(jsonPath("$.airQuality.level", is("GOOD")))
            .andExpect(jsonPath("$.airQuality.levelDescription", is("Good")))
            .andExpect(jsonPath("$.airQuality.pm25", is(12.5)))
            .andExpect(jsonPath("$.airQuality.pm10", is(18.3)))
            .andExpect(jsonPath("$.airQuality.co", is(0.5)))
            .andExpect(jsonPath("$.airQuality.no2", is(15.2)))
            .andExpect(jsonPath("$.airQuality.so2", is(5.1)))
            .andExpect(jsonPath("$.airQuality.o3", is(45.3)))
            .andExpect(jsonPath("$.metadata.dataSource", is("OPENWEATHER_API")))
            .andExpect(jsonPath("$.metadata.fromCache", is(false)))
            .andExpect(jsonPath("$.metadata.retrievedAt", notNullValue()));

        verify(getWeatherUseCase).getCurrentWeather("London", "GB");
    }

    @Test
    @DisplayName("Should get current weather without country parameter")
    void shouldGetCurrentWeatherWithoutCountry() throws Exception {
        // Given
        Location location = Location.builder()
            .city("Paris")
            .latitude(48.8566)
            .longitude(2.3522)
            .build();

        WeatherReading weatherReading = WeatherReading.builder()
            .location(location)
            .temperatureCelsius(18.0)
            .humidity(70.0)
            .pressure(1015.0)
            .windSpeed(3.5)
            .windDirection(90)
            .weatherCondition("Clear")
            .weatherDescription("Clear sky")
            .weatherIcon("01d")
            .visibility(10000.0)
            .cloudiness(0)
            .timestamp(Instant.now())
            .dataSource(WeatherReading.DataSource.OPENWEATHER_API)
            .build();

        GetWeatherUseCase.WeatherResponse response = new GetWeatherUseCase.WeatherResponse(
            weatherReading, null, false
        );

        when(getWeatherUseCase.getCurrentWeather(eq("Paris"), any()))
            .thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/weather")
                .param("city", "Paris")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.location.city", is("Paris")))
            .andExpect(jsonPath("$.weather.temperature", is(18.0)))
            .andExpect(jsonPath("$.weather.condition", is("Clear")));

        verify(getWeatherUseCase).getCurrentWeather(eq("Paris"), any());
    }

    @Test
    @DisplayName("Should get current weather by path parameter")
    void shouldGetCurrentWeatherByPathParameter() throws Exception {
        // Given
        Location location = Location.builder()
            .city("Berlin")
            .country("DE")
            .latitude(52.5200)
            .longitude(13.4050)
            .build();

        WeatherReading weatherReading = WeatherReading.builder()
            .location(location)
            .temperatureCelsius(12.0)
            .humidity(60.0)
            .pressure(1010.0)
            .windSpeed(4.0)
            .windDirection(270)
            .weatherCondition("Rain")
            .weatherDescription("Light rain")
            .weatherIcon("10d")
            .visibility(8000.0)
            .cloudiness(75)
            .timestamp(Instant.now())
            .dataSource(WeatherReading.DataSource.OPENWEATHER_API)
            .build();

        AQIReading aqiReading = AQIReading.builder()
            .location(location)
            .aqi(55)
            .level(AQIReading.AQILevel.MODERATE)
            .pm25(18.5)
            .timestamp(Instant.now())
            .build();

        GetWeatherUseCase.WeatherResponse response = new GetWeatherUseCase.WeatherResponse(
            weatherReading, aqiReading, true
        );

        when(getWeatherUseCase.getCurrentWeather(eq("Berlin"), eq("DE")))
            .thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/weather/current/Berlin")
                .param("country", "DE")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.location.city", is("Berlin")))
            .andExpect(jsonPath("$.location.country", is("DE")))
            .andExpect(jsonPath("$.weather.temperature", is(12.0)))
            .andExpect(jsonPath("$.weather.condition", is("Rain")))
            .andExpect(jsonPath("$.weather.description", is("Light rain")))
            .andExpect(jsonPath("$.airQuality.aqi", is(55)))
            .andExpect(jsonPath("$.airQuality.level", is("MODERATE")))
            .andExpect(jsonPath("$.metadata.fromCache", is(true)));

        verify(getWeatherUseCase).getCurrentWeather("Berlin", "DE");
    }

    @Test
    @DisplayName("Should handle missing city parameter")
    void shouldHandleMissingCityParameter() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle null AQI reading")
    void shouldHandleNullAQIReading() throws Exception {
        // Given
        Location location = Location.builder()
            .city("Tokyo")
            .country("JP")
            .build();

        WeatherReading weatherReading = WeatherReading.builder()
            .location(location)
            .temperatureCelsius(20.0)
            .humidity(55.0)
            .pressure(1012.0)
            .timestamp(Instant.now())
            .dataSource(WeatherReading.DataSource.OPENWEATHER_API)
            .build();

        GetWeatherUseCase.WeatherResponse response = new GetWeatherUseCase.WeatherResponse(
            weatherReading, null, false
        );

        when(getWeatherUseCase.getCurrentWeather(eq("Tokyo"), eq("JP")))
            .thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/weather")
                .param("city", "Tokyo")
                .param("country", "JP")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.location.city", is("Tokyo")))
            .andExpect(jsonPath("$.weather.temperature", is(20.0)))
            .andExpect(jsonPath("$.airQuality").doesNotExist());
    }
}
