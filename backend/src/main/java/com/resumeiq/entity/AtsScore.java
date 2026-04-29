package com.resumeiq.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * ATS score result for a resume analyzed against a job role.
 */
@Document
@Table(name = "ats_scores", indexes = {
    @Index(name = "idx_score_user", columnList = "user_id"),
    @Index(name = "idx_score_resume", columnList = "resume_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AtsScore {
    @Id
    private String id;
    @DBRef(lazy = true)
    private Resume resume;
    @DBRef(lazy = true)
    private User user;
    @DBRef(lazy = true)
    private JobRole jobRole;
    private Double overallScore;  // 0-100
    private Double keywordScore;
    private Double sectionScore;
    private Double formattingScore;
    private Double experienceScore;
    private Double skillsScore;
    private Double readabilityScore;
    private Double projectScore;
    // Role match percentage
    private Double roleMatchPercentage;
    private String missingKeywords;  // JSON array stored as text
    private String matchedKeywords;  // JSON array stored as text
    private String strengths;  // JSON array
    private String weaknesses;  // JSON array
    @CreatedDate
    private LocalDateTime createdAt;
    // Relationships
    @DBRef(lazy = true)
    @Builder.Default
    private List<ScoreBreakdown> breakdowns = new ArrayList<>();
    @DBRef(lazy = true)
    @Builder.Default
    private List<Suggestion> suggestions = new ArrayList<>();
    @DBRef(lazy = true)
    private Report report;
}
