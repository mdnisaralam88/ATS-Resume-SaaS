package com.resumeiq.repository;

import com.resumeiq.entity.ScanHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ScanHistoryRepository extends JpaRepository<ScanHistory, Long> {
    Page<ScanHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<ScanHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(s) FROM ScanHistory s WHERE s.createdAt >= :since")
    long countScansAfter(@Param("since") LocalDateTime since);
}
