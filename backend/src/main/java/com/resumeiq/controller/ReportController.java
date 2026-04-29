package com.resumeiq.controller;

import com.resumeiq.dto.response.ApiResponse;
import com.resumeiq.dto.response.ReportResponse;
import com.resumeiq.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "ATS report generation and download")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate/{atsScoreId}")
    @Operation(summary = "Generate a PDF ATS report for a score")
    public ResponseEntity<ApiResponse<ReportResponse>> generateReport(
            @PathVariable Long atsScoreId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.generateReport(atsScoreId, userDetails.getUsername()), "Report generated"));
    }

    @GetMapping
    @Operation(summary = "Get my reports list")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getMyReports(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getMyReports(userDetails.getUsername())));
    }

    @GetMapping("/download/{reportId}")
    @Operation(summary = "Download a PDF report")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable Long reportId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String filePath = reportService.getReportFilePath(reportId, userDetails.getUsername());
        File file = new File(filePath);
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
