package com.resumeiq.repository;

import com.resumeiq.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {
    Page<Resume> findByUserId(String userId, Pageable pageable);
    Optional<Resume> findByIdAndUserId(String id, String userId);
    long countByUserId(String userId);

    long countByUploadedAtGreaterThanEqual(LocalDateTime since);
}
