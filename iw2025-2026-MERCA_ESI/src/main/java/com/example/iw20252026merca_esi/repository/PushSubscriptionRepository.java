package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findAll();
}