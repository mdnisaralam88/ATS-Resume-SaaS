package com.resumeiq.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
import java.time.LocalDateTime;
@Document
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScanHistory {
    @Id
    private String id;
    @DBRef(lazy = true)
    private User user;
    @DBRef(lazy = true)
    private Resume resume;
    @DBRef(lazy = true)
    private AtsScore atsScore;
    @DBRef(lazy = true)
    private JobRole jobRole;
    @Builder.Default
    private String status = "COMPLETED";  // PENDING, COMPLETED, FAILED
    private String errorMessage;
    @CreatedDate
    private LocalDateTime createdAt;
}
