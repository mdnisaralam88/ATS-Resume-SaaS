package com.resumeiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ATS score result for a resume analyzed against a job role.
 */
@Entity
@Table(name = "ats_scores", indexes = {
    @Index(name = "idx_score_user", columnList = "user_id"),
    @Index(name = "idx_score_resume", columnList = "resume_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AtsScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_role_id", nullable = false)
    private JobRole jobRole;

    @Column(nullable = false)
    private Double overallScore;  // 0-100

    @Column(nullable = false)
    private Double keywordScore;

    @Column(nullable = false)
    private Double sectionScore;

    @Column(nullable = false)
    private Double formattingScore;

    @Column(nullable = false)
    private Double experienceScore;

    @Column(nullable = false)
    private Double skillsScore;

    @Column(nullable = false)
    private Double readabilityScore;

    @Column(nullable = false)
    private Double projectScore;

    // Role match percentage
    @Column(nullable = false)
    private Double roleMatchPercentage;

    @Column(columnDefinition = "TEXT")
    private String missingKeywords;  // JSON array stored as text

    @Column(columnDefinition = "TEXT")
    private String matchedKeywords;  // JSON array stored as text

    @Column(columnDefinition = "TEXT")
    private String strengths;  // JSON array

    @Column(columnDefinition = "TEXT")
    private String weaknesses;  // JSON array

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "atsScore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ScoreBreakdown> breakdowns = new ArrayList<>();

    @OneToMany(mappedBy = "atsScore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Suggestion> suggestions = new ArrayList<>();

    @OneToOne(mappedBy = "atsScore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Report report;
}
