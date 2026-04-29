package com.resumeiq.service;

import com.resumeiq.dto.response.AdminDashboardResponse;
import com.resumeiq.enums.SubscriptionPlan;
import com.resumeiq.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final AtsScoreRepository atsScoreRepository;
    private final ScanHistoryRepository scanHistoryRepository;
    private final SubscriptionRepository subscriptionRepository;

    public AdminDashboardResponse getDashboard() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByRole(com.resumeiq.enums.Role.USER);
        long scansToday = scanHistoryRepository.countScansAfter(startOfToday);
        long totalScans = scanHistoryRepository.count();
        long totalResumes = resumeRepository.count();
        Double avgScore = atsScoreRepository.findAverageScore();
        long freeUsers = subscriptionRepository.countByPlan(SubscriptionPlan.FREE);
        long proUsers = subscriptionRepository.countByPlan(SubscriptionPlan.PRO);
        long premiumUsers = subscriptionRepository.countByPlan(SubscriptionPlan.PREMIUM);

        // Top job roles
        List<Object[]> topRolesRaw = atsScoreRepository.findTopJobRoles();
        List<Map<String, Object>> topRoles = topRolesRaw.stream().limit(6).map(row -> {
            Map<String, Object> m = new HashMap<>();
            m.put("role", row[0]);
            m.put("count", row[1]);
            return m;
        }).collect(Collectors.toList());

        // Daily scan trend (last 7 days)
        List<Map<String, Object>> dailyScans = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dayStart = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            long count = scanHistoryRepository.countScansAfter(dayStart);
            Map<String, Object> day = new HashMap<>();
            day.put("date", LocalDate.now().minusDays(i).toString());
            day.put("scans", count);
            dailyScans.add(day);
        }

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers).activeUsers(activeUsers)
                .scansToday(scansToday).totalScans(totalScans)
                .totalResumes(totalResumes)
                .averageAtsScore(avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0)
                .freeUsers(freeUsers).proUsers(proUsers).premiumUsers(premiumUsers)
                .topJobRoles(topRoles).dailyScans(dailyScans)
                .build();
    }
}
