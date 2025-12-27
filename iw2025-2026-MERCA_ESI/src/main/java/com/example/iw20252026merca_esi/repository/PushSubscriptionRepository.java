// src/main/java/com/example/iw20252026merca_esi/repository/PushSubscriptionRepository.java
package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    Optional<PushSubscription> findByEndpoint(String endpoint);
}