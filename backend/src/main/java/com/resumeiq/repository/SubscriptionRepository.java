package com.resumeiq.repository;

import com.resumeiq.entity.Subscription;
import com.resumeiq.enums.SubscriptionPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    Optional<Subscription> findByUserId(Long userId);

    @Query("SELECT s.plan, COUNT(s) FROM Subscription s GROUP BY s.plan")
    List<Object[]> countByPlan();

    long countByPlan(SubscriptionPlan plan);
}
