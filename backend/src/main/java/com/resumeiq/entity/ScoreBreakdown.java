package com.resumeiq.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
/**
 * Per-category score breakdown for detailed ATS analysis.
 */
@Document
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScoreBreakdown {
    @Id
    private String id;
    @DBRef(lazy = true)
    private AtsScore atsScore;
    private String category;  // e.g., "Keyword Match", "Section Completeness"
    private Double score;     // Achieved score
    private Double maxScore;  // Maximum possible score
    private Integer weight;   // Weight in percent (e.g., 30 for 30%)
    private String details;   // Human-readable explanation
    private String grade;     // A, B, C, D, F
}
