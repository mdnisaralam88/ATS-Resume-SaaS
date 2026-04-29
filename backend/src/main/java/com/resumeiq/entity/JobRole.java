package com.resumeiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Job role template with required skills, tools, and scoring configuration.
 */
@Entity
@Table(name = "job_roles", indexes = {
    @Index(name = "idx_role_slug", columnList = "slug")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;  // URL-friendly identifier

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String experienceLevel;  // Junior, Mid, Senior, All

    @Column(length = 100)
    private String category;  // Software, DevOps, Data, etc.

    @Column(length = 200)
    private String iconName;  // Frontend icon name

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column
    private Integer minExperienceYears;

    @Column
    private Integer maxExperienceYears;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "jobRole", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RoleKeyword> keywords = new ArrayList<>();

    @OneToMany(mappedBy = "jobRole", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AtsScore> atsScores = new ArrayList<>();
}
