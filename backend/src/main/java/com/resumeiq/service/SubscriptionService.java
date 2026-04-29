package com.resumeiq.service;

import com.resumeiq.dto.response.SubscriptionResponse;
import com.resumeiq.entity.Subscription;
import com.resumeiq.entity.User;
import com.resumeiq.enums.NotificationType;
import com.resumeiq.enums.SubscriptionPlan;
import com.resumeiq.exception.ResourceNotFoundException;
import com.resumeiq.exception.ScanLimitExceededException;
import com.resumeiq.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public SubscriptionResponse getMySubscription(String email) {
        User user = userService.findByEmail(email);
        Subscription sub = subscriptionRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        return mapToResponse(sub);
    }

    @Transactional
    public void checkAndIncrementScanCount(Long userId) {
        Subscription sub = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Reset daily count if it's a new day
        if (sub.getLastScanDate() == null || !sub.getLastScanDate().equals(LocalDate.now())) {
            sub.setScansUsedToday(0);
            sub.setLastScanDate(LocalDate.now());
        }

        // Check limit (-1 = unlimited)
        if (sub.getScansLimit() != -1 && sub.getScansUsedToday() >= sub.getScansLimit()) {
            throw new ScanLimitExceededException(
                String.format("Daily scan limit reached (%d/%d). Upgrade to PRO for unlimited scans.",
                    sub.getScansUsedToday(), sub.getScansLimit()));
        }

        sub.setScansUsedToday(sub.getScansUsedToday() + 1);
        sub.setTotalScansUsed(sub.getTotalScansUsed() + 1);
        subscriptionRepository.save(sub);
    }

    @Transactional
    public SubscriptionResponse upgradePlan(String email, String planName) {
        User user = userService.findByEmail(email);
        SubscriptionPlan plan;
        try {
            plan = SubscriptionPlan.valueOf(planName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid subscription plan: " + planName);
        }

        Subscription sub = subscriptionRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        SubscriptionPlan oldPlan = sub.getPlan();
        sub.setPlan(plan);
        sub.setScansLimit(plan == SubscriptionPlan.FREE ? 2 : -1);
        sub.setStartDate(LocalDate.now());
        sub.setRenewalDate(LocalDate.now().plusMonths(1));
        sub.setStatus("ACTIVE");
        subscriptionRepository.save(sub);

        // Update user's subscription plan
        user.setSubscriptionPlan(plan);
        userService.findByEmail(email); // already managed

        notificationService.create(user, NotificationType.PLAN_UPGRADED,
                "Plan Upgraded!",
                "Your plan has been upgraded from " + oldPlan + " to " + plan + ". Enjoy unlimited scans!",
                "/subscription");

        log.info("User {} upgraded from {} to {}", email, oldPlan, plan);
        return mapToResponse(sub);
    }

    public SubscriptionResponse mapToResponse(Subscription s) {
        boolean canScan = s.getScansLimit() == -1 || s.getScansUsedToday() < s.getScansLimit();
        int remaining = s.getScansLimit() == -1 ? Integer.MAX_VALUE :
                Math.max(0, s.getScansLimit() - s.getScansUsedToday());

        return SubscriptionResponse.builder()
                .id(s.getId()).plan(s.getPlan()).status(s.getStatus())
                .scansUsedToday(s.getScansUsedToday()).totalScansUsed(s.getTotalScansUsed())
                .scansLimit(s.getScansLimit()).startDate(s.getStartDate())
                .renewalDate(s.getRenewalDate()).createdAt(s.getCreatedAt())
                .canScan(canScan).remainingScansToday(remaining)
                .build();
    }
}
