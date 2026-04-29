package com.resumeiq.controller;

import com.resumeiq.dto.request.UpdateProfileRequest;
import com.resumeiq.dto.response.ApiResponse;
import com.resumeiq.dto.response.PageResponse;
import com.resumeiq.dto.response.UserResponse;
import com.resumeiq.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(userDetails.getUsername())));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.updateProfile(request, userDetails.getUsername()), "Profile updated successfully"));
    }

    @PostMapping("/me/profile-image")
    @Operation(summary = "Upload profile image")
    public ResponseEntity<ApiResponse<UserResponse>> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return ResponseEntity.ok(ApiResponse.success(
                userService.uploadProfileImage(file, userDetails.getUsername()), "Profile image updated"));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Deactivate my account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "Account deactivated successfully"));
    }
}
