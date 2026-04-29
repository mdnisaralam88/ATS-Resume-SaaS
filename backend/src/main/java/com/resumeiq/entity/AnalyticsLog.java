package com.resumeiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnalyticsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String event;  // e.g., USER_REGISTERED, SCAN_COMPLETED, PLAN_UPGRADED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String metadata;  // JSON string with event-specific data

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 200)
    private String userAgent;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
