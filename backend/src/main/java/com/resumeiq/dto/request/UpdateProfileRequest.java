package com.resumeiq.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
    private String fullName;

    private String phone;
    private String location;
    private String bio;
    private String jobTitle;
    private String company;
    private String linkedinUrl;
    private String githubUrl;
}
