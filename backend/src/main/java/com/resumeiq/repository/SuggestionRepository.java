package com.resumeiq.repository;

import com.resumeiq.entity.Suggestion;
import com.resumeiq.enums.SuggestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    List<Suggestion> findByAtsScoreIdOrderByPriorityAsc(Long atsScoreId);
    List<Suggestion> findByAtsScoreIdAndCategoryOrderByPriorityAsc(Long atsScoreId, SuggestionCategory category);
}
