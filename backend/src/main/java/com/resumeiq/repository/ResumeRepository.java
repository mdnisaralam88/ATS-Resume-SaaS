package com.resumeiq.repository;

import com.resumeiq.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Page<Resume> findByUserId(Long userId, Pageable pageable);
    Optional<Resume> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);

    @Query("SELECT COUNT(r) FROM Resume r WHERE r.uploadedAt >= :since")
    long countUploadedAfter(@Param("since") LocalDateTime since);
}
