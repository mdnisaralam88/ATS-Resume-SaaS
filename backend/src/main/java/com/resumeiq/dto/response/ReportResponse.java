package com.resumeiq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReportResponse {
    private Long id;
    private Long atsScoreId;
    private Double overallScore;
    private String jobRoleName;
    private String resumeFileName;
    private String fileName;
    private Long fileSize;
    private String status;
    private LocalDateTime generatedAt;
    private String downloadUrl;
}
