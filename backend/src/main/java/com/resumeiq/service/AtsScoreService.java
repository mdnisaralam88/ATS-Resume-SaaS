package com.resumeiq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeiq.dto.response.AtsScoreResponse;
import com.resumeiq.dto.response.PageResponse;
import com.resumeiq.dto.response.ScoreBreakdownResponse;
import com.resumeiq.dto.response.SuggestionResponse;
import com.resumeiq.entity.*;
import com.resumeiq.enums.NotificationType;
import com.resumeiq.exception.BadRequestException;
import com.resumeiq.exception.ResourceNotFoundException;
import com.resumeiq.exception.ScanLimitExceededException;
import com.resumeiq.repository.*;
import com.resumeiq.util.AtsScoreCalculator;
import com.resumeiq.util.SuggestionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtsScoreService {

    private final AtsScoreRepository atsScoreRepository;
    private final ScoreBreakdownRepository breakdownRepository;
    private final SuggestionRepository suggestionRepository;
    private final ParsedResumeSectionRepository sectionRepository;
    private final ReportRepository reportRepository;
    private final JobRoleService jobRoleService;
    private final ResumeService resumeService;
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;
    private final AtsScoreCalculator calculator;
    private final SuggestionEngine suggestionEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public AtsScoreResponse analyze(Long resumeId, Long jobRoleId, String email) {
        User user = userService.findByEmail(email);

        // Check scan limit
        subscriptionService.checkAndIncrementScanCount(user.getId());

        Resume resume = resumeService.findById(resumeId);
        if (!resume.getUser().getId().equals(user.getId()))
            throw new BadRequestException("You do not have permission to analyze this resume.");

        if (!resume.isProcessed() || resume.getExtractedText() == null || resume.getExtractedText().isBlank())
            throw new BadRequestException("Resume has not been processed yet. Please re-upload.");

        JobRole jobRole = jobRoleService.findById(jobRoleId);
        List<ParsedResumeSection> sections = sectionRepository.findByResumeId(resumeId);

        // Calculate score
        AtsScoreCalculator.AtsScoreResult result = calculator.calculate(resume.getExtractedText(), sections, jobRole);

        // Build AtsScore entity
        AtsScore atsScore = AtsScore.builder()
                .resume(resume).user(user).jobRole(jobRole)
                .overallScore(result.getOverallScore())
                .keywordScore(result.getKeywordScore())
                .sectionScore(result.getSectionScore())
                .formattingScore(result.getFormattingScore())
                .experienceScore(result.getExperienceScore())
                .skillsScore(result.getSkillsScore())
                .readabilityScore(result.getReadabilityScore())
                .projectScore(result.getProjectScore())
                .roleMatchPercentage(result.getRoleMatchPercentage())
                .missingKeywords(toJson(result.getMissingKeywords()))
                .matchedKeywords(toJson(result.getMatchedKeywords()))
                .strengths(toJson(result.getStrengths()))
                .weaknesses(toJson(result.getWeaknesses()))
                .build();
        atsScore = atsScoreRepository.save(atsScore);

        // Save score breakdowns
        List<ScoreBreakdown> breakdowns = buildBreakdowns(atsScore, result);
        breakdownRepository.saveAll(breakdowns);
        atsScore.setBreakdowns(breakdowns);

        // Generate suggestions
        List<SuggestionEngine.SuggestionResult> suggestionResults = suggestionEngine.generate(result, jobRole, sections);
        List<Suggestion> suggestions = suggestionResults.stream().map(sr -> Suggestion.builder()
                .atsScore(atsScore).category(sr.getCategory())
                .text(sr.getText()).priority(sr.getPriority())
                .section(sr.getSection()).relatedKeyword(sr.getRelatedKeyword())
                .build()).collect(Collectors.toList());
        suggestionRepository.saveAll(suggestions);
        atsScore.setSuggestions(suggestions);

        // Send notification
        notificationService.create(user, NotificationType.SCAN_COMPLETE,
                "ATS Analysis Complete",
                String.format("Your resume scored %.0f/100 for %s role.", result.getOverallScore(), jobRole.getName()),
                "/scores/" + atsScore.getId());

        log.info("ATS analysis complete: score={}, user={}, role={}", result.getOverallScore(), email, jobRole.getName());
        return mapToResponse(atsScore);
    }

    public PageResponse<AtsScoreResponse> getHistory(String email, int page, int size) {
        User user = userService.findByEmail(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AtsScore> scores = atsScoreRepository.findByUserId(user.getId(), pageable);
        return PageResponse.<AtsScoreResponse>builder()
                .content(scores.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(scores.getNumber()).size(scores.getSize())
                .totalElements(scores.getTotalElements()).totalPages(scores.getTotalPages())
                .first(scores.isFirst()).last(scores.isLast()).empty(scores.isEmpty())
                .build();
    }

    public AtsScoreResponse getScoreById(Long id, String email) {
        User user = userService.findByEmail(email);
        AtsScore score = atsScoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Score", id));
        if (!score.getUser().getId().equals(user.getId()))
            throw new BadRequestException("Access denied.");
        score.setBreakdowns(breakdownRepository.findByAtsScoreId(id));
        score.setSuggestions(suggestionRepository.findByAtsScoreIdOrderByPriorityAsc(id));
        return mapToResponse(score);
    }

    public AtsScoreResponse getLatestScore(String email) {
        User user = userService.findByEmail(email);
        AtsScore score = atsScoreRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No scores found. Please analyze a resume first."));
        score.setBreakdowns(breakdownRepository.findByAtsScoreId(score.getId()));
        score.setSuggestions(suggestionRepository.findByAtsScoreIdOrderByPriorityAsc(score.getId()));
        return mapToResponse(score);
    }

    private List<ScoreBreakdown> buildBreakdowns(AtsScore atsScore, AtsScoreCalculator.AtsScoreResult result) {
        return List.of(
            buildBreakdown(atsScore, "Keyword Match",         result.getKeywordScore(),     30, 30),
            buildBreakdown(atsScore, "Section Completeness",  result.getSectionScore(),      20, 20),
            buildBreakdown(atsScore, "Skills Relevance",      result.getSkillsScore(),       20, 20),
            buildBreakdown(atsScore, "Experience Relevance",  result.getExperienceScore(),   10, 10),
            buildBreakdown(atsScore, "Formatting",            result.getFormattingScore(),   10, 10),
            buildBreakdown(atsScore, "Readability",           result.getReadabilityScore(),   5,  5),
            buildBreakdown(atsScore, "Project Relevance",     result.getProjectScore(),       5,  5)
        );
    }

    private ScoreBreakdown buildBreakdown(AtsScore atsScore, String category, double score, int maxScore, int weight) {
        double actualScore = score * maxScore / 100.0;
        String grade = actualScore >= maxScore * 0.9 ? "A" : actualScore >= maxScore * 0.7 ? "B"
                : actualScore >= maxScore * 0.5 ? "C" : actualScore >= maxScore * 0.3 ? "D" : "F";
        return ScoreBreakdown.builder()
                .atsScore(atsScore).category(category)
                .score(Math.round(actualScore * 100.0) / 100.0)
                .maxScore(maxScore).weight(weight).grade(grade)
                .details(String.format("%.0f%% of maximum %d points", score, maxScore))
                .build();
    }

    public AtsScoreResponse mapToResponse(AtsScore s) {
        boolean reportGenerated = reportRepository.findByAtsScoreId(s.getId()).isPresent();
        Long reportId = reportRepository.findByAtsScoreId(s.getId()).map(Report::getId).orElse(null);

        List<ScoreBreakdownResponse> breakdowns = s.getBreakdowns() == null ? List.of() :
                s.getBreakdowns().stream().map(b -> ScoreBreakdownResponse.builder()
                        .id(b.getId()).category(b.getCategory()).score(b.getScore())
                        .maxScore(b.getMaxScore()).weight(b.getWeight()).details(b.getDetails())
                        .grade(b.getGrade()).percentage(b.getMaxScore() > 0 ? b.getScore() / b.getMaxScore() * 100 : 0)
                        .build()).collect(Collectors.toList());

        List<SuggestionResponse> suggestions = s.getSuggestions() == null ? List.of() :
                s.getSuggestions().stream().map(sg -> SuggestionResponse.builder()
                        .id(sg.getId()).category(sg.getCategory().name()).text(sg.getText())
                        .priority(sg.getPriority()).section(sg.getSection()).relatedKeyword(sg.getRelatedKeyword())
                        .build()).collect(Collectors.toList());

        String grade = s.getOverallScore() >= 90 ? "A+" : s.getOverallScore() >= 80 ? "A"
                : s.getOverallScore() >= 70 ? "B" : s.getOverallScore() >= 60 ? "C"
                : s.getOverallScore() >= 50 ? "D" : "F";

        return AtsScoreResponse.builder()
                .id(s.getId()).resumeId(s.getResume().getId())
                .resumeFileName(s.getResume().getOriginalFileName())
                .userId(s.getUser().getId()).jobRoleId(s.getJobRole().getId())
                .jobRoleName(s.getJobRole().getName())
                .overallScore(s.getOverallScore()).keywordScore(s.getKeywordScore())
                .sectionScore(s.getSectionScore()).formattingScore(s.getFormattingScore())
                .experienceScore(s.getExperienceScore()).skillsScore(s.getSkillsScore())
                .readabilityScore(s.getReadabilityScore()).projectScore(s.getProjectScore())
                .roleMatchPercentage(s.getRoleMatchPercentage())
                .missingKeywords(fromJson(s.getMissingKeywords()))
                .matchedKeywords(fromJson(s.getMatchedKeywords()))
                .strengths(fromJson(s.getStrengths()))
                .weaknesses(fromJson(s.getWeaknesses()))
                .breakdowns(breakdowns).suggestions(suggestions)
                .grade(grade).reportGenerated(reportGenerated).reportId(reportId)
                .createdAt(s.getCreatedAt())
                .build();
    }

    private String toJson(List<String> list) {
        try { return objectMapper.writeValueAsString(list != null ? list : List.of()); }
        catch (JsonProcessingException e) { return "[]"; }
    }

    @SuppressWarnings("unchecked")
    private List<String> fromJson(String json) {
        try { return json != null ? objectMapper.readValue(json, List.class) : List.of(); }
        catch (Exception e) { return List.of(); }
    }
}
