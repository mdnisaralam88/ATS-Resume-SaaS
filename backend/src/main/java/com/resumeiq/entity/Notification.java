package com.resumeiq.entity;
import com.resumeiq.enums.NotificationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
import java.time.LocalDateTime;
@Document
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id
    private String id;
    @DBRef(lazy = true)
    private User user;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private String title;
    private String message;
    @Builder.Default
    private boolean isRead = false;
    private String actionUrl;
    @CreatedDate
    private LocalDateTime createdAt;
}
