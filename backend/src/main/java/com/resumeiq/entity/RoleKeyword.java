package com.resumeiq.entity;
import com.resumeiq.enums.KeywordType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
/**
 * Keyword associated with a job role for ATS matching.
 */
@Document
@Table(name = "role_keywords", indexes = {
    @Index(name = "idx_keyword_role", columnList = "job_role_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleKeyword {
    @Id
    private String id;
    @DBRef(lazy = true)
    private JobRole jobRole;
    private String keyword;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private KeywordType type = KeywordType.REQUIRED;
    @Column
    @Builder.Default
    private Integer weight = 1;  // Higher weight = more impact on score
    private String category;  // e.g., "Programming Language", "Framework", "Database"
}
