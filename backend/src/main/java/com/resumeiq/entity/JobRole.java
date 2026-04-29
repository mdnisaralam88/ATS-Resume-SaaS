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
 * Job role template with required skills, tools, and scoring configuration.
 */
@Document
@Table(name = "job_roles", indexes = {
    @Index(name = "idx_role_slug", columnList = "slug")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobRole {
    @Id
    private String id;
    private String name;
    private String slug;  // URL-friendly identifier
    private String description;
    private String experienceLevel;  // Junior, Mid, Senior, All
    private String category;  // Software, DevOps, Data, etc.
    private String iconName;  // Frontend icon name
    @Builder.Default
    private boolean isActive = true;
    @Column
    private Integer minExperienceYears;
    @Column
    private Integer maxExperienceYears;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    // Relationships
    @DBRef(lazy = true)
    @Builder.Default
    private List<RoleKeyword> keywords = new ArrayList<>();
    @DBRef(lazy = true)
    @Builder.Default
    private List<AtsScore> atsScores = new ArrayList<>();
}
