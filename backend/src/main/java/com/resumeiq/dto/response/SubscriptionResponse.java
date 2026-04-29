package com.resumeiq.dto.response;

import com.resumeiq.enums.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private SubscriptionPlan plan;
    private String status;
    private Integer scansUsedToday;
    private Integer totalScansUsed;
    private Integer scansLimit;
    private LocalDate startDate;
    private LocalDate renewalDate;
    private LocalDateTime createdAt;
    private boolean canScan;
    private Integer remainingScansToday;
}
