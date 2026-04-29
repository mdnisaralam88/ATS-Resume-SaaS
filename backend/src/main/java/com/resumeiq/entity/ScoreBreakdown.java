package com.resumeiq.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Per-category score breakdown for detailed ATS analysis.
 */
@Entity
@Table(name = "score_breakdowns")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScoreBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ats_score_id", nullable = false)
    private AtsScore atsScore;

    @Column(nullable = false, length = 50)
    private String category;  // e.g., "Keyword Match", "Section Completeness"

    @Column(nullable = false)
    private Double score;     // Achieved score

    @Column(nullable = false)
    private Double maxScore;  // Maximum possible score

    @Column(nullable = false)
    private Integer weight;   // Weight in percent (e.g., 30 for 30%)

    @Column(length = 500)
    private String details;   // Human-readable explanation

    @Column(length = 20)
    private String grade;     // A, B, C, D, F
}
