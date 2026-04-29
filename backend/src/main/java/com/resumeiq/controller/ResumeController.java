package com.resumeiq.controller;

import com.resumeiq.dto.response.ApiResponse;
import com.resumeiq.dto.response.PageResponse;
import com.resumeiq.dto.response.ResumeResponse;
import com.resumeiq.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
@Tag(name = "Resumes", description = "Resume upload, parsing, and management")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a resume file (PDF or DOCX)")
    public ResponseEntity<ApiResponse<ResumeResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(resumeService.upload(file, title, userDetails.getUsername()),
                        "Resume uploaded and parsed successfully"));
    }

    @GetMapping
    @Operation(summary = "Get my resumes (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<ResumeResponse>>> getMyResumes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                resumeService.getMyResumes(userDetails.getUsername(), page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a resume by ID with parsed sections")
    public ResponseEntity<ApiResponse<ResumeResponse>> getResume(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                resumeService.getResumeById(id, userDetails.getUsername())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resume")
    public ResponseEntity<ApiResponse<Void>> deleteResume(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        resumeService.deleteResume(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "Resume deleted successfully"));
    }
}
