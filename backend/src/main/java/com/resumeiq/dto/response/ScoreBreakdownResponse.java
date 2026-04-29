package com.resumeiq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ScoreBreakdownResponse {
    private Long id;
    private String category;
    private Double score;
    private Double maxScore;
    private Integer weight;
    private String details;
    private String grade;
    private Double percentage;
}
