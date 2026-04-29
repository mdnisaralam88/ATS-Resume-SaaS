package com.resumeiq.repository;

import com.resumeiq.entity.ScanHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ScanHistoryRepository extends MongoRepository<ScanHistory, String> {
    Page<ScanHistory> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    Page<ScanHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByCreatedAtGreaterThanEqual(LocalDateTime since);
}
