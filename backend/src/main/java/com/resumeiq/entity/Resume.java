package com.resumeiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Uploaded resume file with extracted text and metadata.
 */
@Entity
@Table(name = "resumes", indexes = {
    @Index(name = "idx_resume_user", columnList = "user_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String originalFileName;

    @Column(nullable = false, length = 10)
    private String fileType;  // PDF or DOCX

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 500)
    private String filePath;

    @Column(columnDefinition = "LONGTEXT")
    private String extractedText;

    @Column(length = 100)
    private String candidateName;

    @Column(length = 150)
    private String candidateEmail;

    @Column(length = 30)
    private String candidatePhone;

    @Column(length = 100)
    private String title;  // User-defined label for this resume

    @Column(nullable = false)
    @Builder.Default
    private boolean processed = false;

    @Column(length = 500)
    private String processingError;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    // Relationships
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ParsedResumeSection> sections = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AtsScore> atsScores = new ArrayList<>();
}
