package com.resumeiq.entity;

import com.resumeiq.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User subscription record tracking plan, usage, and billing dates.
 */
@Entity
@Table(name = "subscriptions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubscriptionPlan plan = SubscriptionPlan.FREE;

    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";  // ACTIVE, CANCELLED, EXPIRED, TRIAL

    @Column(nullable = false)
    @Builder.Default
    private Integer scansUsedToday = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalScansUsed = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer scansLimit = 2;  // FREE = 2/day

    private LocalDate lastScanDate;

    private LocalDate startDate;

    private LocalDate renewalDate;

    private LocalDate cancelledDate;

    @Column(length = 100)
    private String stripeCustomerId;  // For future real Stripe integration

    @Column(length = 100)
    private String stripeSubscriptionId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
