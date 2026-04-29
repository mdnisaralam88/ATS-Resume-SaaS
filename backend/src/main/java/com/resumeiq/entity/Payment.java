package com.resumeiq.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Mock payment record for subscription purchases.
 */
@Document
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_user", columnList = "user_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id
    private String id;
    @DBRef(lazy = true)
    private User user;
    @DBRef(lazy = true)
    private Subscription subscription;
    private BigDecimal amount;
    @Builder.Default
    private String currency = "USD";
    @Builder.Default
    private String status = "COMPLETED";  // PENDING, COMPLETED, FAILED, REFUNDED
    private String paymentMethod;  // MOCK, STRIPE, etc.
    private String transactionId;
    private String plan;  // Plan purchased
    private String description;
    @CreatedDate
    private LocalDateTime createdAt;
}
