package com.resumeiq.entity;
import com.resumeiq.enums.Role;
import com.resumeiq.enums.SubscriptionPlan;
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
 * Core user entity with full profile and account information.
 */
@Document
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    private String id;
    private String fullName;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    private String profileImage;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.FREE;
    @Builder.Default
    private boolean emailVerified = false;
    private String emailVerificationToken;
    private LocalDateTime emailVerificationExpiry;
    private String passwordResetToken;
    private LocalDateTime passwordResetExpiry;
    @Builder.Default
    private boolean active = true;
    private String phone;
    private String location;
    private String bio;
    private String jobTitle;
    private String company;
    private String linkedinUrl;
    private String githubUrl;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    // Relationships
    @DBRef(lazy = true)
    @Builder.Default
    private List<Resume> resumes = new ArrayList<>();
    @DBRef(lazy = true)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
    @DBRef(lazy = true)
    private Subscription subscription;
}
