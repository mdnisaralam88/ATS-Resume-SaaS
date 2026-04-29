package com.resumeiq.repository;

import com.resumeiq.entity.ScoreBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScoreBreakdownRepository extends JpaRepository<ScoreBreakdown, Long> {
    List<ScoreBreakdown> findByAtsScoreId(Long atsScoreId);
}
