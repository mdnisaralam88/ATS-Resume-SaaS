package com.resumeiq.dto.response;

import com.resumeiq.enums.Role;
import com.resumeiq.enums.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String profileImage;
    private SubscriptionPlan subscriptionPlan;
    private boolean emailVerified;
    private boolean active;
    private String phone;
    private String location;
    private String bio;
    private String jobTitle;
    private String company;
    private String linkedinUrl;
    private String githubUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
