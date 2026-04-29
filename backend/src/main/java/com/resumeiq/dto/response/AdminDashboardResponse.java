package com.resumeiq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminDashboardResponse {
    private Long totalUsers;
    private Long activeUsers;
    private Long scansToday;
    private Long totalScans;
    private Long totalResumes;
    private Double averageAtsScore;
    private Long freeUsers;
    private Long proUsers;
    private Long premiumUsers;
    private List<Map<String, Object>> topJobRoles;
    private List<Map<String, Object>> recentScans;
    private List<Map<String, Object>> dailyScans;
    private List<Map<String, Object>> userGrowth;
}
