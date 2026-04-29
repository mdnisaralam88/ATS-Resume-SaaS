package com.resumeiq.util;

import com.resumeiq.entity.JobRole;
import com.resumeiq.entity.ParsedResumeSection;
import com.resumeiq.enums.SectionType;
import com.resumeiq.enums.SuggestionCategory;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SuggestionEngine {

    public List<SuggestionResult> generate(AtsScoreCalculator.AtsScoreResult scoreResult,
                                            JobRole jobRole,
                                            List<ParsedResumeSection> sections) {
        List<SuggestionResult> suggestions = new ArrayList<>();
        int priority = 1;

        // ── CRITICAL suggestions ──────────────────────────────────────────────
        // Missing required keywords
        List<String> missing = scoreResult.getMissingKeywords();
        if (!missing.isEmpty()) {
            int limit = Math.min(5, missing.size());
            for (int i = 0; i < limit; i++) {
                String kw = missing.get(i);
                suggestions.add(SuggestionResult.builder()
                        .category(SuggestionCategory.CRITICAL)
                        .text("Add the keyword \"" + kw + "\" — it is required for the " + jobRole.getName() + " role and currently missing from your resume.")
                        .priority(priority++)
                        .relatedKeyword(kw)
                        .section("SKILLS")
                        .build());
            }
        }

        // Missing sections
        boolean hasExperience = sections.stream().anyMatch(s -> s.getSectionType() == SectionType.EXPERIENCE);
        boolean hasSkills = sections.stream().anyMatch(s -> s.getSectionType() == SectionType.SKILLS);
        boolean hasEducation = sections.stream().anyMatch(s -> s.getSectionType() == SectionType.EDUCATION);

        if (!hasExperience) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.CRITICAL)
                    .text("Add a Work Experience section. ATS systems rank resumes without experience sections very low.")
                    .priority(priority++).section("EXPERIENCE").build());
        }
        if (!hasSkills) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.CRITICAL)
                    .text("Add a dedicated Skills section with your technical stack. Most ATS systems specifically scan this section.")
                    .priority(priority++).section("SKILLS").build());
        }
        if (!hasEducation) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.CRITICAL)
                    .text("Add an Education section with your degree, institution, and graduation year.")
                    .priority(priority++).section("EDUCATION").build());
        }

        // Low keyword score
        if (scoreResult.getKeywordScore() < 40) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.CRITICAL)
                    .text("Your keyword match rate is critically low (" + (int) scoreResult.getKeywordScore() + "%). Review the job description and incorporate role-specific terms throughout your resume.")
                    .priority(priority++).build());
        }

        // ── RECOMMENDED suggestions ───────────────────────────────────────────
        boolean hasProjects = sections.stream().anyMatch(s -> s.getSectionType() == SectionType.PROJECTS);
        boolean hasSummary = sections.stream().anyMatch(s -> s.getSectionType() == SectionType.SUMMARY);
        boolean hasCertifications = sections.stream().anyMatch(s -> s.getSectionType() == SectionType.CERTIFICATIONS);

        if (!hasProjects) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.RECOMMENDED)
                    .text("Add a Projects section showcasing 2-3 relevant projects. Include technologies used, your role, and measurable outcomes.")
                    .priority(priority++).section("PROJECTS").build());
        }
        if (!hasSummary) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.RECOMMENDED)
                    .text("Add a Professional Summary at the top (3-5 sentences). Include your years of experience, key skills, and career goal aligned with the target role.")
                    .priority(priority++).section("SUMMARY").build());
        }

        if (scoreResult.getExperienceScore() < 50) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.RECOMMENDED)
                    .text("Quantify your achievements with metrics (e.g., \"Improved API response time by 40%\", \"Served 10,000+ users\"). Measurable results significantly improve ATS and recruiter scores.")
                    .priority(priority++).section("EXPERIENCE").build());
        }

        // Add more missing keywords as recommended
        if (missing.size() > 5) {
            int start = 5;
            int end = Math.min(10, missing.size());
            String kwList = String.join(", ", missing.subList(start, end));
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.RECOMMENDED)
                    .text("Consider adding these additional role-relevant keywords to your resume: " + kwList + ".")
                    .priority(priority++).section("SKILLS").build());
        }

        if (scoreResult.getFormattingScore() < 60) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.RECOMMENDED)
                    .text("Ensure your resume includes your email address, phone number, and LinkedIn profile for maximum ATS compatibility.")
                    .priority(priority++).section("CONTACT").build());
        }

        // Strong action verbs recommendation
        suggestions.add(SuggestionResult.builder()
                .category(SuggestionCategory.RECOMMENDED)
                .text("Start each experience bullet with strong action verbs: Developed, Architected, Optimized, Led, Delivered, Designed, Implemented, Reduced, Increased.")
                .priority(priority++).section("EXPERIENCE").build());

        // ── OPTIONAL suggestions ──────────────────────────────────────────────
        if (!hasCertifications) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.OPTIONAL)
                    .text("Add relevant certifications (e.g., AWS Certified Developer, Google Cloud, Oracle Java). Certifications strengthen your profile and ATS score.")
                    .priority(priority++).section("CERTIFICATIONS").build());
        }

        suggestions.add(SuggestionResult.builder()
                .category(SuggestionCategory.OPTIONAL)
                .text("Add your GitHub profile URL to showcase your open-source contributions and personal projects.")
                .priority(priority++).build());

        suggestions.add(SuggestionResult.builder()
                .category(SuggestionCategory.OPTIONAL)
                .text("Tailor your resume summary specifically for the " + jobRole.getName() + " role by mentioning the role title and key responsibilities.")
                .priority(priority++).section("SUMMARY").build());

        if (scoreResult.getOverallScore() >= 70) {
            suggestions.add(SuggestionResult.builder()
                    .category(SuggestionCategory.OPTIONAL)
                    .text("Your resume is well-optimized. Consider adding a portfolio website link or publications section to further differentiate yourself.")
                    .priority(priority++).build());
        }

        log.debug("Generated {} suggestions for role: {}", suggestions.size(), jobRole.getName());
        return suggestions;
    }

    @Data
    @Builder
    public static class SuggestionResult {
        private SuggestionCategory category;
        private String text;
        private Integer priority;
        private String section;
        private String relatedKeyword;
    }
}
