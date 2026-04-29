package com.resumeiq.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
import java.time.LocalDateTime;
/**
 * Generated ATS analysis PDF report.
 */
@Document
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Report {
    @Id
    private String id;
    @DBRef(lazy = true)
    private AtsScore atsScore;
    @DBRef(lazy = true)
    private User user;
    private String filePath;
    private String fileName;
    @Column
    private Long fileSize;
    @Builder.Default
    private String status = "GENERATED";
    @CreatedDate
    private LocalDateTime generatedAt;
}
