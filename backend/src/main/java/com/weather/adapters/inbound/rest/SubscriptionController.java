package com.weather.adapters.inbound.rest;

import com.weather.adapters.inbound.rest.dto.SubscriptionRequestDTO;
import com.weather.adapters.inbound.rest.dto.SubscriptionResponseDTO;
import com.weather.application.port.input.ManageSubscriptionUseCase;
import com.weather.domain.model.Subscription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for subscription management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Weather alert subscription endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SubscriptionController {

    private final ManageSubscriptionUseCase manageSubscriptionUseCase;

    @PostMapping
    @Operation(summary = "Create subscription", description = "Creates a new weather alert subscription")
    public ResponseEntity<SubscriptionResponseDTO> createSubscription(
        @Valid @RequestBody SubscriptionRequestDTO requestDTO
    ) {
        log.info("REST request - Create subscription for user: {}", requestDTO.getUserId());

        Subscription subscription = requestDTO.toDomain();
        Subscription created = manageSubscriptionUseCase.createSubscription(subscription);

        SubscriptionResponseDTO response = SubscriptionResponseDTO.fromDomain(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscription", description = "Updates an existing subscription")
    public ResponseEntity<SubscriptionResponseDTO> updateSubscription(
        @Parameter(description = "Subscription ID", required = true, example = "1")
        @PathVariable Long id,

        @Valid @RequestBody SubscriptionRequestDTO requestDTO
    ) {
        log.info("REST request - Update subscription ID: {}", id);

        Subscription subscription = requestDTO.toDomain();
        Subscription updated = manageSubscriptionUseCase.updateSubscription(id, subscription);

        SubscriptionResponseDTO response = SubscriptionResponseDTO.fromDomain(updated);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subscription", description = "Deletes a subscription by ID")
    public ResponseEntity<Void> deleteSubscription(
        @Parameter(description = "Subscription ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        log.info("REST request - Delete subscription ID: {}", id);

        manageSubscriptionUseCase.deleteSubscription(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription", description = "Retrieves a subscription by ID")
    public ResponseEntity<SubscriptionResponseDTO> getSubscription(
        @Parameter(description = "Subscription ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        log.info("REST request - Get subscription ID: {}", id);

        Subscription subscription = manageSubscriptionUseCase.getSubscription(id);

        SubscriptionResponseDTO response = SubscriptionResponseDTO.fromDomain(subscription);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user subscriptions", description = "Retrieves all subscriptions for a user")
    public ResponseEntity<List<SubscriptionResponseDTO>> getUserSubscriptions(
        @Parameter(description = "User ID", required = true, example = "user123")
        @PathVariable String userId
    ) {
        log.info("REST request - Get subscriptions for user: {}", userId);

        List<Subscription> subscriptions = manageSubscriptionUseCase.getUserSubscriptions(userId);

        List<SubscriptionResponseDTO> response = subscriptions.stream()
            .map(SubscriptionResponseDTO::fromDomain)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
