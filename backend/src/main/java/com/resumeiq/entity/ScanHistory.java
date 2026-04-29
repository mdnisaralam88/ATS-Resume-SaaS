package com.resumeiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scan_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ats_score_id")
    private AtsScore atsScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_role_id")
    private JobRole jobRole;

    @Column(nullable = false)
    @Builder.Default
    private String status = "COMPLETED";  // PENDING, COMPLETED, FAILED

    @Column(length = 500)
    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
