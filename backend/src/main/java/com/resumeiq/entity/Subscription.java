package com.resumeiq.entity;
import com.resumeiq.enums.SubscriptionPlan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * User subscription record tracking plan, usage, and billing dates.
 */
@Document
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subscription {
    @Id
    private String id;
    @DBRef(lazy = true)
    private User user;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubscriptionPlan plan = SubscriptionPlan.FREE;
    @Builder.Default
    private String status = "ACTIVE";  // ACTIVE, CANCELLED, EXPIRED, TRIAL
    @Builder.Default
    private Integer scansUsedToday = 0;
    @Builder.Default
    private Integer totalScansUsed = 0;
    @Builder.Default
    private Integer scansLimit = 2;  // FREE = 2/day
    private LocalDate lastScanDate;
    private LocalDate startDate;
    private LocalDate renewalDate;
    private LocalDate cancelledDate;
    private String stripeCustomerId;  // For future real Stripe integration
    private String stripeSubscriptionId;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
