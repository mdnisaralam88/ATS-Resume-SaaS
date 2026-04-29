package com.resumeiq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AtsScoreResponse {
    private Long id;
    private Long resumeId;
    private String resumeFileName;
    private Long userId;
    private Long jobRoleId;
    private String jobRoleName;
    private Double overallScore;
    private Double keywordScore;
    private Double sectionScore;
    private Double formattingScore;
    private Double experienceScore;
    private Double skillsScore;
    private Double readabilityScore;
    private Double projectScore;
    private Double roleMatchPercentage;
    private List<String> missingKeywords;
    private List<String> matchedKeywords;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<ScoreBreakdownResponse> breakdowns;
    private List<SuggestionResponse> suggestions;
    private String grade;
    private boolean reportGenerated;
    private Long reportId;
    private LocalDateTime createdAt;
}
