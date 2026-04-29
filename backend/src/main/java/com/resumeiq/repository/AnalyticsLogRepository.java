package com.resumeiq.repository;

import com.resumeiq.entity.AnalyticsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsLogRepository extends JpaRepository<AnalyticsLog, Long> {
    Page<AnalyticsLog> findByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a.event, COUNT(a) FROM AnalyticsLog a WHERE a.createdAt >= :since GROUP BY a.event ORDER BY COUNT(a) DESC")
    List<Object[]> countEventsSince(@Param("since") LocalDateTime since);
}
