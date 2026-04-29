package com.resumeiq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ResumeResponse {
    private Long id;
    private Long userId;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String title;
    private boolean processed;
    private LocalDateTime uploadedAt;
    private List<SectionResponse> sections;
    private Long latestScoreId;
    private Double latestScore;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SectionResponse {
        private Long id;
        private String sectionType;
        private String content;
        private Integer wordCount;
        private Float qualityScore;
    }
}
