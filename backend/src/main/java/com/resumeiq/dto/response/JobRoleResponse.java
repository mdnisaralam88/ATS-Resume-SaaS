package com.resumeiq.dto.response;

import com.resumeiq.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class JobRoleResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String experienceLevel;
    private String category;
    private String iconName;
    private boolean isActive;
    private Integer minExperienceYears;
    private Integer maxExperienceYears;
    private List<KeywordResponse> keywords;
    private LocalDateTime createdAt;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class KeywordResponse {
        private Long id;
        private String keyword;
        private KeywordType type;
        private Integer weight;
        private String category;
    }
}
