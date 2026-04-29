package com.resumeiq.repository;

import com.resumeiq.entity.ScoreBreakdown;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScoreBreakdownRepository extends MongoRepository<ScoreBreakdown, String> {
    List<ScoreBreakdown> findByAtsScoreId(Long atsScoreId);
}
