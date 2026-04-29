package com.resumeiq.repository;

import com.resumeiq.entity.AtsScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtsScoreRepository extends MongoRepository<AtsScore, String> {
    Page<AtsScore> findByUserId(Long userId, Pageable pageable);
    Optional<AtsScore> findTopByUserIdOrderByCreatedAtDesc(Long userId);
    List<AtsScore> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT AVG(a.overallScore) FROM AtsScore a")
    Double findAverageScore();

    @Query("SELECT AVG(a.overallScore) FROM AtsScore a WHERE a.user.id = :userId")
    Double findAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM AtsScore a WHERE a.createdAt >= :since")
    long countScansAfter(@Param("since") LocalDateTime since);

    @Query("SELECT a.jobRole.name, COUNT(a) FROM AtsScore a GROUP BY a.jobRole.name ORDER BY COUNT(a) DESC")
    List<Object[]> findTopJobRoles();

    @Query("SELECT a FROM AtsScore a WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    List<AtsScore> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
}
