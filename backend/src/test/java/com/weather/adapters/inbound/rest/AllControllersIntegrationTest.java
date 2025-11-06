package com.weather.adapters.inbound.rest;

import com.weather.application.port.input.GetForecastUseCase;
import com.weather.application.port.input.GetHistoryUseCase;
import com.weather.application.port.input.GetWeatherUseCase;
import com.weather.application.port.input.ManageSubscriptionUseCase;
import com.weather.domain.model.AQIReading;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test to verify all REST endpoints are accessible
 */
@DisplayName("All Controllers Integration Test")
class AllControllersIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private GetWeatherUseCase getWeatherUseCase;

    @MockBean
    private GetForecastUseCase getForecastUseCase;

    @MockBean
    private GetHistoryUseCase getHistoryUseCase;

    @MockBean
    private ManageSubscriptionUseCase manageSubscriptionUseCase;

    @BeforeEach
    void setUpMocks() {
        // Mock weather use case
        Location location = Location.builder().city("London").build();
        WeatherReading weather = WeatherReading.builder()
            .location(location)
            .temperatureCelsius(15.0)
            .timestamp(Instant.now())
            .build();
        AQIReading aqi = AQIReading.builder()
            .location(location)
            .aqi(50)
            .timestamp(Instant.now())
            .build();
        GetWeatherUseCase.WeatherResponse weatherResponse =
            new GetWeatherUseCase.WeatherResponse(weather, aqi, false);
        when(getWeatherUseCase.getCurrentWeather(anyString(), any())).thenReturn(weatherResponse);

        // Mock forecast use case
        when(getForecastUseCase.getForecast(anyString(), any(), anyInt()))
            .thenReturn(Collections.emptyList());

        // Mock history use cases
        when(getHistoryUseCase.getWeatherHistory(anyString(), any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(getHistoryUseCase.getAQIHistory(anyString(), any(), any(), any()))
            .thenReturn(Collections.emptyList());

        // Mock subscription use case
        when(manageSubscriptionUseCase.getUserSubscriptions(anyString()))
            .thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("Should have weather endpoints accessible")
    void shouldHaveWeatherEndpointsAccessible() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/weather")
                .param("city", "London"))
            .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/weather/current/London"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should have forecast endpoints accessible")
    void shouldHaveForecastEndpointsAccessible() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast")
                .param("city", "London"))
            .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast/London"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should have history endpoints accessible")
    void shouldHaveHistoryEndpointsAccessible() throws Exception {
        String from = "2024-01-01T00:00:00Z";
        String to = "2024-01-31T23:59:59Z";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/weather")
                .param("city", "London")
                .param("from", from)
                .param("to", to))
            .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/aqi")
                .param("city", "London")
                .param("from", from)
                .param("to", to))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should have subscription endpoints accessible")
    void shouldHaveSubscriptionEndpointsAccessible() throws Exception {
        // GET endpoints should work without authentication in test mode
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/subscriptions/user/test123"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 404 for non-existent endpoints")
    void shouldReturn404ForNonExistentEndpoints() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/nonexistent"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for missing required parameters")
    void shouldReturn400ForMissingRequiredParameters() throws Exception {
        // Weather endpoint requires city
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/weather"))
            .andExpect(status().isBadRequest());

        // Forecast endpoint requires city
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/forecast"))
            .andExpect(status().isBadRequest());

        // History endpoint requires city, from, and to
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/history/weather"))
            .andExpect(status().isBadRequest());
    }
}
