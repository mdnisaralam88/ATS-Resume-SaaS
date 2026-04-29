package com.resumeiq.dto.request;

import com.resumeiq.enums.KeywordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateJobRoleRequest {
    @NotBlank(message = "Role name is required")
    private String name;

    private String description;
    private String experienceLevel;
    private String category;
    private String iconName;
    private Integer minExperienceYears;
    private Integer maxExperienceYears;
    private List<KeywordRequest> keywords;

    @Data
    public static class KeywordRequest {
        @NotBlank
        private String keyword;
        @NotNull
        private KeywordType type;
        private Integer weight = 1;
        private String category;
    }
}
