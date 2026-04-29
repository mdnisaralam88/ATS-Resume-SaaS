package com.resumeiq.repository;

import com.resumeiq.entity.Suggestion;
import com.resumeiq.enums.SuggestionCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestionRepository extends MongoRepository<Suggestion, String> {
    List<Suggestion> findByAtsScoreIdOrderByPriorityAsc(Long atsScoreId);
    List<Suggestion> findByAtsScoreIdAndCategoryOrderByPriorityAsc(Long atsScoreId, SuggestionCategory category);
}
