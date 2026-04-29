package com.resumeiq.repository;

import com.resumeiq.entity.Subscription;
import com.resumeiq.enums.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserId(Long userId);

    @Query("SELECT s.plan, COUNT(s) FROM Subscription s GROUP BY s.plan")
    List<Object[]> countByPlan();

    long countByPlan(SubscriptionPlan plan);
}
