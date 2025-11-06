package com.weather.application.port.input;

import com.weather.domain.model.Subscription;

import java.util.List;

/**
 * Input port for managing alert subscriptions
 */
public interface ManageSubscriptionUseCase {

    /**
     * Creates a new subscription
     */
    Subscription createSubscription(Subscription subscription);

    /**
     * Updates an existing subscription
     */
    Subscription updateSubscription(Long id, Subscription subscription);

    /**
     * Deletes a subscription
     */
    void deleteSubscription(Long id);

    /**
     * Gets all subscriptions for a user
     */
    List<Subscription> getUserSubscriptions(String userId);

    /**
     * Gets a specific subscription by ID
     */
    Subscription getSubscription(Long id);
}
