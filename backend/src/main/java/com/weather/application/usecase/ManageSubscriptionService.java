package com.weather.application.usecase;

import com.weather.application.port.input.ManageSubscriptionUseCase;
import com.weather.application.port.output.StoragePort;
import com.weather.domain.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageSubscriptionService implements ManageSubscriptionUseCase {

    private final StoragePort storage;

    @Override
    public Subscription createSubscription(Subscription subscription) {
        log.info("Creating subscription for user: {}", subscription.getUserId());
        subscription.setCreatedAt(Instant.now());
        subscription.setActive(true);
        return storage.saveSubscription(subscription);
    }

    @Override
    public Subscription updateSubscription(Long id, Subscription subscription) {
        log.info("Updating subscription: {}", id);
        Subscription existing = storage.getSubscriptionById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + id));

        subscription.setId(id);
        subscription.setCreatedAt(existing.getCreatedAt());
        return storage.saveSubscription(subscription);
    }

    @Override
    public void deleteSubscription(Long id) {
        log.info("Deleting subscription: {}", id);
        storage.deleteSubscription(id);
    }

    @Override
    public List<Subscription> getUserSubscriptions(String userId) {
        log.debug("Fetching subscriptions for user: {}", userId);
        return storage.getSubscriptionsByUserId(userId);
    }

    @Override
    public Subscription getSubscription(Long id) {
        return storage.getSubscriptionById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + id));
    }
}
