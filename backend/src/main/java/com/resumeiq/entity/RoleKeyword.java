package com.resumeiq.entity;

import com.resumeiq.enums.KeywordType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Keyword associated with a job role for ATS matching.
 */
@Entity
@Table(name = "role_keywords", indexes = {
    @Index(name = "idx_keyword_role", columnList = "job_role_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_role_id", nullable = false)
    private JobRole jobRole;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private KeywordType type = KeywordType.REQUIRED;

    @Column
    @Builder.Default
    private Integer weight = 1;  // Higher weight = more impact on score

    @Column(length = 100)
    private String category;  // e.g., "Programming Language", "Framework", "Database"
}
