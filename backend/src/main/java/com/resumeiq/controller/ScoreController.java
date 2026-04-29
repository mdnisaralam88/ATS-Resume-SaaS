package com.resumeiq.controller;

import com.resumeiq.dto.request.AnalyzeResumeRequest;
import com.resumeiq.dto.response.ApiResponse;
import com.resumeiq.dto.response.AtsScoreResponse;
import com.resumeiq.dto.response.PageResponse;
import com.resumeiq.service.AtsScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
@Tag(name = "ATS Scores", description = "Resume analysis, scoring, and history")
public class ScoreController {

    private final AtsScoreService atsScoreService;

    @PostMapping("/analyze")
    @Operation(summary = "Analyze a resume against a job role and get ATS score")
    public ResponseEntity<ApiResponse<AtsScoreResponse>> analyze(
            @Valid @RequestBody AnalyzeResumeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                atsScoreService.analyze(request.getResumeId(), request.getJobRoleId(), userDetails.getUsername()),
                "Analysis complete"));
    }

    @GetMapping
    @Operation(summary = "Get my score history (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<AtsScoreResponse>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                atsScoreService.getHistory(userDetails.getUsername(), page, size)));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get my latest ATS score")
    public ResponseEntity<ApiResponse<AtsScoreResponse>> getLatest(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                atsScoreService.getLatestScore(userDetails.getUsername())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full score detail by ID")
    public ResponseEntity<ApiResponse<AtsScoreResponse>> getScore(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                atsScoreService.getScoreById(id, userDetails.getUsername())));
    }
}
