package com.resumeiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mock payment record for subscription purchases.
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_user", columnList = "user_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "USD";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "COMPLETED";  // PENDING, COMPLETED, FAILED, REFUNDED

    @Column(length = 50)
    private String paymentMethod;  // MOCK, STRIPE, etc.

    @Column(length = 200)
    private String transactionId;

    @Column(length = 50)
    private String plan;  // Plan purchased

    @Column(length = 200)
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
