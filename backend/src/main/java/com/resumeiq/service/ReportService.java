package com.resumeiq.service;

import com.resumeiq.dto.response.ReportResponse;
import com.resumeiq.entity.AtsScore;
import com.resumeiq.entity.Report;
import com.resumeiq.entity.User;
import com.resumeiq.enums.NotificationType;
import com.resumeiq.exception.BadRequestException;
import com.resumeiq.exception.ResourceNotFoundException;
import com.resumeiq.repository.AtsScoreRepository;
import com.resumeiq.repository.ReportRepository;
import com.resumeiq.repository.ScoreBreakdownRepository;
import com.resumeiq.repository.SuggestionRepository;
import com.resumeiq.util.PdfReportGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final AtsScoreRepository atsScoreRepository;
    private final ScoreBreakdownRepository breakdownRepository;
    private final SuggestionRepository suggestionRepository;
    private final PdfReportGenerator pdfReportGenerator;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public ReportResponse generateReport(Long atsScoreId, String email) {
        User user = userService.findByEmail(email);

        AtsScore atsScore = atsScoreRepository.findById(atsScoreId)
                .orElseThrow(() -> new ResourceNotFoundException("ATS Score", atsScoreId));

        if (!atsScore.getUser().getId().equals(user.getId()))
            throw new BadRequestException("Access denied.");

        // Check if report already exists
        if (reportRepository.findByAtsScoreId(atsScoreId).isPresent()) {
            return mapToResponse(reportRepository.findByAtsScoreId(atsScoreId).get());
        }

        // Fetch breakdowns and suggestions for the report
        atsScore.setBreakdowns(breakdownRepository.findByAtsScoreId(atsScoreId));
        atsScore.setSuggestions(suggestionRepository.findByAtsScoreIdOrderByPriorityAsc(atsScoreId));

        try {
            String filePath = pdfReportGenerator.generateReport(atsScore);
            File file = new File(filePath);

            Report report = Report.builder()
                    .atsScore(atsScore).user(user)
                    .filePath(filePath)
                    .fileName(file.getName())
                    .fileSize(file.length())
                    .status("GENERATED")
                    .build();
            report = reportRepository.save(report);

            notificationService.create(user, NotificationType.REPORT_READY,
                    "ATS Report Ready",
                    "Your ATS analysis report is ready for download.",
                    "/reports");

            return mapToResponse(report);
        } catch (Exception e) {
            log.error("Failed to generate report: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to generate report: " + e.getMessage());
        }
    }

    public List<ReportResponse> getMyReports(String email) {
        User user = userService.findByEmail(email);
        Pageable pageable = PageRequest.of(0, 50, Sort.by("generatedAt").descending());
        return reportRepository.findByUserId(user.getId(), pageable)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public String getReportFilePath(Long reportId, String email) {
        User user = userService.findByEmail(email);
        Report report = reportRepository.findByIdAndUserId(reportId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Report", reportId));
        return report.getFilePath();
    }

    public ReportResponse mapToResponse(Report r) {
        return ReportResponse.builder()
                .id(r.getId())
                .atsScoreId(r.getAtsScore().getId())
                .overallScore(r.getAtsScore().getOverallScore())
                .jobRoleName(r.getAtsScore().getJobRole().getName())
                .resumeFileName(r.getAtsScore().getResume().getOriginalFileName())
                .fileName(r.getFileName()).fileSize(r.getFileSize())
                .status(r.getStatus()).generatedAt(r.getGeneratedAt())
                .downloadUrl("/api/v1/reports/download/" + r.getId())
                .build();
    }
}
