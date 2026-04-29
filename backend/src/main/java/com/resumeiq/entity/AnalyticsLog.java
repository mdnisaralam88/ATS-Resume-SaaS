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
public class AnalyticsLog {
    @Id
    private String id;
    private String event;  // e.g., USER_REGISTERED, SCAN_COMPLETED, PLAN_UPGRADED
    @DBRef(lazy = true)
    private User user;
    private String metadata;  // JSON string with event-specific data
    private String ipAddress;
    private String userAgent;
    @CreatedDate
    private LocalDateTime createdAt;
}
