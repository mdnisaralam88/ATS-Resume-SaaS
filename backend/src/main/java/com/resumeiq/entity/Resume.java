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
 * Uploaded resume file with extracted text and metadata.
 */
@Document
@Table(name = "resumes", indexes = {
    @Index(name = "idx_resume_user", columnList = "user_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Resume {
    @Id
    private String id;
    @DBRef(lazy = true)
    private User user;
    private String originalFileName;
    private String fileType;  // PDF or DOCX
    private Long fileSize;
    private String filePath;
    private String extractedText;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String title;  // User-defined label for this resume
    @Builder.Default
    private boolean processed = false;
    private String processingError;
    @CreatedDate
    private LocalDateTime uploadedAt;
    // Relationships
    @DBRef(lazy = true)
    @Builder.Default
    private List<ParsedResumeSection> sections = new ArrayList<>();
    @DBRef(lazy = true)
    @Builder.Default
    private List<AtsScore> atsScores = new ArrayList<>();
}
