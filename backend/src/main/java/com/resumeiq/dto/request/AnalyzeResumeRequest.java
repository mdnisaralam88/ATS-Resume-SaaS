package com.resumeiq.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnalyzeResumeRequest {
    @NotNull(message = "Resume ID is required")
    private Long resumeId;

    @NotNull(message = "Job role ID is required")
    private Long jobRoleId;
}
