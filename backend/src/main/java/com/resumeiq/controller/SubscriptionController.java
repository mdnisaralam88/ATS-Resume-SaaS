package com.resumeiq.controller;

import com.resumeiq.dto.response.ApiResponse;
import com.resumeiq.dto.response.SubscriptionResponse;
import com.resumeiq.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscription plan management")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/me")
    @Operation(summary = "Get my current subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                subscriptionService.getMySubscription(userDetails.getUsername())));
    }

    @PostMapping("/upgrade")
    @Operation(summary = "Upgrade subscription plan (mock payment)")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> upgradePlan(
            @RequestParam String plan,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                subscriptionService.upgradePlan(userDetails.getUsername(), plan), "Plan upgraded successfully"));
    }

    @GetMapping("/plans")
    @Operation(summary = "Get available subscription plans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPlans() {
        List<Map<String, Object>> plans = List.of(
            Map.of("name", "FREE", "price", 0, "scansPerDay", 2, "features",
                List.of("2 scans/day", "Basic ATS score", "Keyword analysis")),
            Map.of("name", "PRO", "price", 9.99, "scansPerDay", -1, "features",
                List.of("Unlimited scans", "Full ATS breakdown", "PDF reports", "Priority support")),
            Map.of("name", "PREMIUM", "price", 19.99, "scansPerDay", -1, "features",
                List.of("Everything in Pro", "Advanced analytics", "Role comparison", "API access", "Custom reports"))
        );
        return ResponseEntity.ok(ApiResponse.success(plans));
    }
}
