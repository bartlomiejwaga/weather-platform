package com.weather.adapters.outbound.persistence.repository;

import com.weather.adapters.outbound.persistence.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {

    List<SubscriptionEntity> findByUserId(String userId);

    List<SubscriptionEntity> findByActiveTrue();

    List<SubscriptionEntity> findByUserIdAndActiveTrue(String userId);
}
