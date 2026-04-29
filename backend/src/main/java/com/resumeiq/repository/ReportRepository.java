package com.resumeiq.repository;

import com.resumeiq.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    Page<Report> findByUserId(Long userId, Pageable pageable);
    Optional<Report> findByAtsScoreId(Long atsScoreId);
    Optional<Report> findByIdAndUserId(Long id, Long userId);
}
