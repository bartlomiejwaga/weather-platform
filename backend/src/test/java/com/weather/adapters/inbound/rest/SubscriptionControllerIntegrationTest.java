package com.weather.adapters.inbound.rest;

import com.weather.adapters.inbound.rest.dto.SubscriptionRequestDTO;
import com.weather.application.port.input.ManageSubscriptionUseCase;
import com.weather.domain.model.Location;
import com.weather.domain.model.Subscription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SubscriptionController
 */
@DisplayName("Subscription Controller Integration Tests")
class SubscriptionControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private ManageSubscriptionUseCase manageSubscriptionUseCase;

    @Test
    @DisplayName("Should create subscription")
    void shouldCreateSubscription() throws Exception {
        // Given
        SubscriptionRequestDTO requestDTO = createSubscriptionRequestDTO(
            "user123",
            "user@example.com",
            "London",
            "GB",
            new HashSet<>(Arrays.asList(
                Subscription.AlertType.HIGH_TEMPERATURE,
                Subscription.AlertType.POOR_AIR_QUALITY
            )),
            30.0,
            null,
            150
        );

        Subscription createdSubscription = createSubscription(
            1L,
            "user123",
            "user@example.com",
            "London",
            "GB",
            new HashSet<>(Arrays.asList(
                Subscription.AlertType.HIGH_TEMPERATURE,
                Subscription.AlertType.POOR_AIR_QUALITY
            )),
            30.0,
            null,
            150,
            true,
            Instant.now(),
            null
        );

        when(manageSubscriptionUseCase.createSubscription(any(Subscription.class)))
            .thenReturn(createdSubscription);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.userId", is("user123")))
            .andExpect(jsonPath("$.email", is("user@example.com")))
            .andExpect(jsonPath("$.location.city", is("London")))
            .andExpect(jsonPath("$.location.country", is("GB")))
            .andExpect(jsonPath("$.alertTypes", hasSize(2)))
            .andExpect(jsonPath("$.alertTypes", hasItem("HIGH_TEMPERATURE")))
            .andExpect(jsonPath("$.alertTypes", hasItem("POOR_AIR_QUALITY")))
            .andExpect(jsonPath("$.thresholds.maxTemperature", is(30.0)))
            .andExpect(jsonPath("$.thresholds.maxAQI", is(150)))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(manageSubscriptionUseCase).createSubscription(any(Subscription.class));
    }

    @Test
    @DisplayName("Should validate required fields on create")
    void shouldValidateRequiredFieldsOnCreate() throws Exception {
        // Given - missing userId
        SubscriptionRequestDTO requestDTO = SubscriptionRequestDTO.builder()
            .email("user@example.com")
            .location(SubscriptionRequestDTO.LocationDTO.builder()
                .city("London")
                .build())
            .alertTypes(new HashSet<>(Collections.singletonList(Subscription.AlertType.HIGH_TEMPERATURE)))
            .build();

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest());

        verify(manageSubscriptionUseCase, never()).createSubscription(any());
    }

    @Test
    @DisplayName("Should validate email format on create")
    void shouldValidateEmailFormatOnCreate() throws Exception {
        // Given - invalid email
        SubscriptionRequestDTO requestDTO = SubscriptionRequestDTO.builder()
            .userId("user123")
            .email("invalid-email")
            .location(SubscriptionRequestDTO.LocationDTO.builder()
                .city("London")
                .build())
            .alertTypes(new HashSet<>(Collections.singletonList(Subscription.AlertType.HIGH_TEMPERATURE)))
            .build();

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest());

        verify(manageSubscriptionUseCase, never()).createSubscription(any());
    }

    @Test
    @DisplayName("Should update subscription")
    void shouldUpdateSubscription() throws Exception {
        // Given
        Long subscriptionId = 1L;
        SubscriptionRequestDTO requestDTO = createSubscriptionRequestDTO(
            "user123",
            "user@example.com",
            "London",
            "GB",
            new HashSet<>(Arrays.asList(
                Subscription.AlertType.HIGH_TEMPERATURE,
                Subscription.AlertType.LOW_TEMPERATURE,
                Subscription.AlertType.POOR_AIR_QUALITY
            )),
            35.0,
            -5.0,
            200
        );

        Subscription updatedSubscription = createSubscription(
            subscriptionId,
            "user123",
            "user@example.com",
            "London",
            "GB",
            new HashSet<>(Arrays.asList(
                Subscription.AlertType.HIGH_TEMPERATURE,
                Subscription.AlertType.LOW_TEMPERATURE,
                Subscription.AlertType.POOR_AIR_QUALITY
            )),
            35.0,
            -5.0,
            200,
            true,
            Instant.now().minusSeconds(86400),
            null
        );

        when(manageSubscriptionUseCase.updateSubscription(eq(subscriptionId), any(Subscription.class)))
            .thenReturn(updatedSubscription);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/subscriptions/" + subscriptionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.userId", is("user123")))
            .andExpect(jsonPath("$.alertTypes", hasSize(3)))
            .andExpect(jsonPath("$.alertTypes", hasItem("HIGH_TEMPERATURE")))
            .andExpect(jsonPath("$.alertTypes", hasItem("LOW_TEMPERATURE")))
            .andExpect(jsonPath("$.alertTypes", hasItem("POOR_AIR_QUALITY")))
            .andExpect(jsonPath("$.thresholds.maxTemperature", is(35.0)))
            .andExpect(jsonPath("$.thresholds.minTemperature", is(-5.0)))
            .andExpect(jsonPath("$.thresholds.maxAQI", is(200)));

        verify(manageSubscriptionUseCase).updateSubscription(eq(subscriptionId), any(Subscription.class));
    }

    @Test
    @DisplayName("Should delete subscription")
    void shouldDeleteSubscription() throws Exception {
        // Given
        Long subscriptionId = 1L;

        doNothing().when(manageSubscriptionUseCase).deleteSubscription(subscriptionId);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/subscriptions/" + subscriptionId))
            .andExpect(status().isNoContent());

        verify(manageSubscriptionUseCase).deleteSubscription(subscriptionId);
    }

    @Test
    @DisplayName("Should get subscription by ID")
    void shouldGetSubscriptionById() throws Exception {
        // Given
        Long subscriptionId = 1L;
        Subscription subscription = createSubscription(
            subscriptionId,
            "user123",
            "user@example.com",
            "London",
            "GB",
            new HashSet<>(Collections.singletonList(Subscription.AlertType.HIGH_TEMPERATURE)),
            30.0,
            null,
            null,
            true,
            Instant.now(),
            null
        );

        when(manageSubscriptionUseCase.getSubscription(subscriptionId))
            .thenReturn(subscription);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/subscriptions/" + subscriptionId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.userId", is("user123")))
            .andExpect(jsonPath("$.email", is("user@example.com")))
            .andExpect(jsonPath("$.location.city", is("London")))
            .andExpect(jsonPath("$.location.country", is("GB")))
            .andExpect(jsonPath("$.active", is(true)));

        verify(manageSubscriptionUseCase).getSubscription(subscriptionId);
    }

    @Test
    @DisplayName("Should get user subscriptions")
    void shouldGetUserSubscriptions() throws Exception {
        // Given
        String userId = "user123";
        List<Subscription> subscriptions = Arrays.asList(
            createSubscription(
                1L,
                userId,
                "user@example.com",
                "London",
                "GB",
                new HashSet<>(Collections.singletonList(Subscription.AlertType.HIGH_TEMPERATURE)),
                30.0,
                null,
                null,
                true,
                Instant.now(),
                null
            ),
            createSubscription(
                2L,
                userId,
                "user@example.com",
                "Paris",
                "FR",
                new HashSet<>(Collections.singletonList(Subscription.AlertType.POOR_AIR_QUALITY)),
                null,
                null,
                150,
                true,
                Instant.now(),
                null
            ),
            createSubscription(
                3L,
                userId,
                "user@example.com",
                "Berlin",
                "DE",
                new HashSet<>(Arrays.asList(
                    Subscription.AlertType.HIGH_WIND,
                    Subscription.AlertType.HEAVY_RAIN
                )),
                null,
                null,
                null,
                false,
                Instant.now(),
                Instant.now().minusSeconds(3600)
            )
        );

        when(manageSubscriptionUseCase.getUserSubscriptions(userId))
            .thenReturn(subscriptions);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/subscriptions/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].location.city", is("London")))
            .andExpect(jsonPath("$[0].active", is(true)))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].location.city", is("Paris")))
            .andExpect(jsonPath("$[1].active", is(true)))
            .andExpect(jsonPath("$[2].id", is(3)))
            .andExpect(jsonPath("$[2].location.city", is("Berlin")))
            .andExpect(jsonPath("$[2].active", is(false)))
            .andExpect(jsonPath("$[2].alertTypes", hasSize(2)))
            .andExpect(jsonPath("$[2].alertTypes", hasItem("HIGH_WIND")))
            .andExpect(jsonPath("$[2].alertTypes", hasItem("HEAVY_RAIN")))
            .andExpect(jsonPath("$[2].lastNotifiedAt", notNullValue()));

        verify(manageSubscriptionUseCase).getUserSubscriptions(userId);
    }

    @Test
    @DisplayName("Should handle empty user subscriptions list")
    void shouldHandleEmptyUserSubscriptionsList() throws Exception {
        // Given
        String userId = "user456";

        when(manageSubscriptionUseCase.getUserSubscriptions(userId))
            .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/subscriptions/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(manageSubscriptionUseCase).getUserSubscriptions(userId);
    }

    @Test
    @DisplayName("Should create subscription with all alert types")
    void shouldCreateSubscriptionWithAllAlertTypes() throws Exception {
        // Given
        SubscriptionRequestDTO requestDTO = createSubscriptionRequestDTO(
            "user123",
            "user@example.com",
            "London",
            "GB",
            new HashSet<>(Arrays.asList(
                Subscription.AlertType.HIGH_TEMPERATURE,
                Subscription.AlertType.LOW_TEMPERATURE,
                Subscription.AlertType.POOR_AIR_QUALITY,
                Subscription.AlertType.EXTREME_WEATHER,
                Subscription.AlertType.HIGH_WIND,
                Subscription.AlertType.HEAVY_RAIN,
                Subscription.AlertType.UV_WARNING
            )),
            35.0,
            -10.0,
            200
        );

        Subscription createdSubscription = createSubscription(
            1L,
            "user123",
            "user@example.com",
            "London",
            "GB",
            requestDTO.getAlertTypes(),
            35.0,
            -10.0,
            200,
            true,
            Instant.now(),
            null
        );

        when(manageSubscriptionUseCase.createSubscription(any(Subscription.class)))
            .thenReturn(createdSubscription);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.alertTypes", hasSize(7)))
            .andExpect(jsonPath("$.alertTypes", hasItem("HIGH_TEMPERATURE")))
            .andExpect(jsonPath("$.alertTypes", hasItem("LOW_TEMPERATURE")))
            .andExpect(jsonPath("$.alertTypes", hasItem("POOR_AIR_QUALITY")))
            .andExpect(jsonPath("$.alertTypes", hasItem("EXTREME_WEATHER")))
            .andExpect(jsonPath("$.alertTypes", hasItem("HIGH_WIND")))
            .andExpect(jsonPath("$.alertTypes", hasItem("HEAVY_RAIN")))
            .andExpect(jsonPath("$.alertTypes", hasItem("UV_WARNING")));
    }

    @Test
    @DisplayName("Should create inactive subscription")
    void shouldCreateInactiveSubscription() throws Exception {
        // Given
        SubscriptionRequestDTO requestDTO = createSubscriptionRequestDTO(
            "user123",
            "user@example.com",
            "London",
            "GB",
            new HashSet<>(Collections.singletonList(Subscription.AlertType.HIGH_TEMPERATURE)),
            30.0,
            null,
            null
        );
        requestDTO.setActive(false);

        Subscription createdSubscription = createSubscription(
            1L,
            "user123",
            "user@example.com",
            "London",
            "GB",
            new HashSet<>(Collections.singletonList(Subscription.AlertType.HIGH_TEMPERATURE)),
            30.0,
            null,
            null,
            false,
            Instant.now(),
            null
        );

        when(manageSubscriptionUseCase.createSubscription(any(Subscription.class)))
            .thenReturn(createdSubscription);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.active", is(false)));
    }

    // Helper methods
    private SubscriptionRequestDTO createSubscriptionRequestDTO(String userId, String email,
                                                                 String city, String country,
                                                                 java.util.Set<Subscription.AlertType> alertTypes,
                                                                 Double maxTemp, Double minTemp, Integer maxAQI) {
        return SubscriptionRequestDTO.builder()
            .userId(userId)
            .email(email)
            .location(SubscriptionRequestDTO.LocationDTO.builder()
                .city(city)
                .country(country)
                .build())
            .alertTypes(alertTypes)
            .thresholds(SubscriptionRequestDTO.AlertThresholdsDTO.builder()
                .maxTemperature(maxTemp)
                .minTemperature(minTemp)
                .maxAQI(maxAQI)
                .build())
            .active(true)
            .build();
    }

    private Subscription createSubscription(Long id, String userId, String email,
                                            String city, String country,
                                            java.util.Set<Subscription.AlertType> alertTypes,
                                            Double maxTemp, Double minTemp, Integer maxAQI,
                                            boolean active, Instant createdAt, Instant lastNotifiedAt) {
        return Subscription.builder()
            .id(id)
            .userId(userId)
            .email(email)
            .location(Location.builder()
                .city(city)
                .country(country)
                .build())
            .alertTypes(alertTypes)
            .thresholds(Subscription.AlertThresholds.builder()
                .maxTemperature(maxTemp)
                .minTemperature(minTemp)
                .maxAQI(maxAQI)
                .build())
            .active(active)
            .createdAt(createdAt)
            .lastNotifiedAt(lastNotifiedAt)
            .build();
    }
}
