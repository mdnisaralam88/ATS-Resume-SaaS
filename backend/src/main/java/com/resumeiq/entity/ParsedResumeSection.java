package com.resumeiq.entity;

import com.resumeiq.enums.SectionType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Individual parsed section from a resume (skills, experience, education, etc).
 */
@Entity
@Table(name = "parsed_resume_sections", indexes = {
    @Index(name = "idx_section_resume", columnList = "resume_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParsedResumeSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SectionType sectionType;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column
    private Integer wordCount;

    @Column
    private Float qualityScore;  // 0-100 individual section quality
}
