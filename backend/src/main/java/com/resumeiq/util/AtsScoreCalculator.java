package com.resumeiq.util;

import com.resumeiq.entity.JobRole;
import com.resumeiq.entity.ParsedResumeSection;
import com.resumeiq.entity.RoleKeyword;
import com.resumeiq.enums.KeywordType;
import com.resumeiq.enums.SectionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Core ATS scoring engine.
 * Evaluates resumes against job roles using a weighted multi-category scoring model.
 */
@Component
@Slf4j
public class AtsScoreCalculator {

    // Scoring weights (must total 100)
    private static final double KEYWORD_WEIGHT        = 0.30;
    private static final double SECTION_WEIGHT        = 0.20;
    private static final double SKILLS_WEIGHT         = 0.20;
    private static final double EXPERIENCE_WEIGHT     = 0.10;
    private static final double FORMATTING_WEIGHT     = 0.10;
    private static final double READABILITY_WEIGHT    = 0.05;
    private static final double PROJECT_WEIGHT        = 0.05;

    private static final Set<SectionType> REQUIRED_SECTIONS = Set.of(
        SectionType.SUMMARY, SectionType.SKILLS, SectionType.EXPERIENCE,
        SectionType.EDUCATION, SectionType.PROJECTS
    );

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    public AtsScoreResult calculate(String resumeText,
                                    List<ParsedResumeSection> sections,
                                    JobRole jobRole) {
        String normalizedText = normalizeText(resumeText);
        List<RoleKeyword> keywords = jobRole.getKeywords();

        // 1. Keyword match score
        KeywordMatchResult kwResult = calculateKeywordScore(normalizedText, keywords);

        // 2. Section completeness
        double sectionScore  = calculateSectionScore(sections);

        // 3. Skills relevance
        double skillsScore   = calculateSkillsScore(sections, keywords);

        // 4. Experience relevance
        double experienceScore = calculateExperienceScore(sections, resumeText, jobRole);

        // 5. Formatting score
        double formattingScore = calculateFormattingScore(resumeText, sections);

        // 6. Readability score
        double readabilityScore = calculateReadabilityScore(resumeText);

        // 7. Project relevance
        double projectScore  = calculateProjectScore(sections, keywords);

        // Weighted overall score
        double overallScore =
              (kwResult.score       * KEYWORD_WEIGHT)
            + (sectionScore        * SECTION_WEIGHT)
            + (skillsScore         * SKILLS_WEIGHT)
            + (experienceScore     * EXPERIENCE_WEIGHT)
            + (formattingScore     * FORMATTING_WEIGHT)
            + (readabilityScore    * READABILITY_WEIGHT)
            + (projectScore        * PROJECT_WEIGHT);

        overallScore = Math.min(100, Math.max(0, overallScore));

        // Role match %
        double roleMatch = calculateRoleMatch(kwResult, skillsScore);

        // Strengths and weaknesses
        List<String> strengths   = buildStrengths(kwResult, sectionScore, skillsScore, formattingScore);
        List<String> weaknesses  = buildWeaknesses(kwResult, sectionScore, skillsScore, formattingScore, sections);

        return AtsScoreResult.builder()
                .overallScore(round(overallScore))
                .keywordScore(round(kwResult.score))
                .sectionScore(round(sectionScore))
                .formattingScore(round(formattingScore))
                .experienceScore(round(experienceScore))
                .skillsScore(round(skillsScore))
                .readabilityScore(round(readabilityScore))
                .projectScore(round(projectScore))
                .roleMatchPercentage(round(roleMatch))
                .missingKeywords(kwResult.missing)
                .matchedKeywords(kwResult.matched)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Keyword Match (30%)
    // ─────────────────────────────────────────────────────────────────────────

    private KeywordMatchResult calculateKeywordScore(String text, List<RoleKeyword> keywords) {
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        if (keywords.isEmpty()) {
            return new KeywordMatchResult(50.0, matched, missing);
        }

        double totalWeight = 0;
        double matchedWeight = 0;

        for (RoleKeyword rk : keywords) {
            double weight = rk.getWeight() != null ? rk.getWeight() : 1;
            // Required keywords worth more
            if (rk.getType() == KeywordType.REQUIRED) weight *= 2;

            totalWeight += weight;

            if (containsKeyword(text, rk.getKeyword())) {
                matchedWeight += weight;
                matched.add(rk.getKeyword());
            } else {
                missing.add(rk.getKeyword());
            }
        }

        double score = totalWeight > 0 ? (matchedWeight / totalWeight) * 100 : 0;
        return new KeywordMatchResult(score, matched, missing);
    }

    private boolean containsKeyword(String text, String keyword) {
        String normalizedKw = normalizeText(keyword);
        // Whole-word match with word boundaries
        String pattern = "(?i)\\b" + Pattern.quote(normalizedKw) + "\\b";
        return Pattern.compile(pattern).matcher(text).find();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Section Completeness (20%)
    // ─────────────────────────────────────────────────────────────────────────

    private double calculateSectionScore(List<ParsedResumeSection> sections) {
        Set<SectionType> presentSections = sections.stream()
                .map(ParsedResumeSection::getSectionType)
                .collect(Collectors.toSet());

        long present = REQUIRED_SECTIONS.stream()
                .filter(presentSections::contains)
                .count();

        double baseScore = ((double) present / REQUIRED_SECTIONS.size()) * 80;

        // Bonus: check section quality (non-empty, adequate content)
        double qualityBonus = sections.stream()
                .filter(s -> REQUIRED_SECTIONS.contains(s.getSectionType()))
                .mapToDouble(s -> {
                    int words = s.getWordCount() != null ? s.getWordCount() : countWords(s.getContent());
                    return words >= 20 ? 4.0 : (words >= 5 ? 2.0 : 0.0);
                })
                .sum();

        return Math.min(100, baseScore + qualityBonus);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Skills Relevance (20%)
    // ─────────────────────────────────────────────────────────────────────────

    private double calculateSkillsScore(List<ParsedResumeSection> sections, List<RoleKeyword> keywords) {
        Optional<ParsedResumeSection> skillsSection = sections.stream()
                .filter(s -> s.getSectionType() == SectionType.SKILLS)
                .findFirst();

        if (skillsSection.isEmpty() || skillsSection.get().getContent() == null) return 0;

        String skillsText = normalizeText(skillsSection.get().getContent());
        List<RoleKeyword> requiredSkills = keywords.stream()
                .filter(k -> k.getType() == KeywordType.REQUIRED || k.getType() == KeywordType.PREFERRED)
                .collect(Collectors.toList());

        if (requiredSkills.isEmpty()) return 70;

        long matched = requiredSkills.stream()
                .filter(k -> containsKeyword(skillsText, k.getKeyword()))
                .count();

        return ((double) matched / requiredSkills.size()) * 100;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Experience Relevance (10%)
    // ─────────────────────────────────────────────────────────────────────────

    private double calculateExperienceScore(List<ParsedResumeSection> sections,
                                             String resumeText, JobRole jobRole) {
        boolean hasExperience = sections.stream()
                .anyMatch(s -> s.getSectionType() == SectionType.EXPERIENCE
                        && s.getContent() != null && s.getContent().length() > 50);

        if (!hasExperience) return 10;

        double score = 40; // Base: has experience section

        // Check for quantifiable achievements (numbers, %)
        long numberCount = Pattern.compile("\\d+[%+x]?\\s*(users|customers|revenue|performance|efficiency|reduction|improvement|increase|million|billion|k)",
                Pattern.CASE_INSENSITIVE)
                .matcher(resumeText).results().count();
        score += Math.min(30, numberCount * 10);

        // Check for action verbs
        String[] actionVerbs = {"led", "built", "developed", "designed", "implemented", "managed",
                "architected", "optimized", "reduced", "increased", "achieved", "delivered", "created"};
        long verbCount = Arrays.stream(actionVerbs)
                .filter(v -> Pattern.compile("(?i)\\b" + v + "\\b").matcher(resumeText).find())
                .count();
        score += Math.min(30, verbCount * 3);

        return Math.min(100, score);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Formatting Score (10%)
    // ─────────────────────────────────────────────────────────────────────────

    private double calculateFormattingScore(String resumeText, List<ParsedResumeSection> sections) {
        double score = 0;
        int totalWords = countWords(resumeText);

        // Ideal resume length: 300-800 words
        if (totalWords >= 300 && totalWords <= 800) score += 30;
        else if (totalWords >= 200 && totalWords < 300) score += 20;
        else if (totalWords > 800 && totalWords <= 1200) score += 20;
        else score += 10;

        // Has multiple sections
        score += Math.min(30, sections.size() * 6);

        // Email present
        if (Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}").matcher(resumeText).find())
            score += 15;

        // Phone number present
        if (Pattern.compile("(\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}").matcher(resumeText).find())
            score += 15;

        // LinkedIn / GitHub
        if (resumeText.toLowerCase().contains("linkedin")) score += 5;
        if (resumeText.toLowerCase().contains("github")) score += 5;

        return Math.min(100, score);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Readability Score (5%)
    // ─────────────────────────────────────────────────────────────────────────

    private double calculateReadabilityScore(String text) {
        String[] sentences = text.split("[.!?]+");
        if (sentences.length == 0) return 0;

        double avgWords = Arrays.stream(sentences)
                .mapToInt(s -> countWords(s))
                .average().orElse(20);

        // Ideal: 10-20 words per sentence
        if (avgWords >= 10 && avgWords <= 20) return 90;
        else if (avgWords < 10) return 70;
        else if (avgWords <= 30) return 60;
        else return 40;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Project Relevance (5%)
    // ─────────────────────────────────────────────────────────────────────────

    private double calculateProjectScore(List<ParsedResumeSection> sections, List<RoleKeyword> keywords) {
        Optional<ParsedResumeSection> projectSection = sections.stream()
                .filter(s -> s.getSectionType() == SectionType.PROJECTS)
                .findFirst();

        if (projectSection.isEmpty() || projectSection.get().getContent() == null) return 20;

        String projectText = normalizeText(projectSection.get().getContent());
        List<RoleKeyword> toolKeywords = keywords.stream()
                .filter(k -> k.getType() == KeywordType.TOOL)
                .collect(Collectors.toList());

        if (toolKeywords.isEmpty()) return 60;

        long matched = toolKeywords.stream()
                .filter(k -> containsKeyword(projectText, k.getKeyword()))
                .count();

        double base = 40;
        double matchScore = ((double) matched / toolKeywords.size()) * 60;
        return Math.min(100, base + matchScore);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Role Match
    // ─────────────────────────────────────────────────────────────────────────

    private double calculateRoleMatch(KeywordMatchResult kwResult, double skillsScore) {
        int totalKeywords = kwResult.matched.size() + kwResult.missing.size();
        if (totalKeywords == 0) return 50;
        double kwMatch = (double) kwResult.matched.size() / totalKeywords * 100;
        return (kwMatch * 0.6) + (skillsScore * 0.4);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Strengths & Weaknesses
    // ─────────────────────────────────────────────────────────────────────────

    private List<String> buildStrengths(KeywordMatchResult kw, double sectionScore,
                                         double skillsScore, double formattingScore) {
        List<String> strengths = new ArrayList<>();
        if (kw.matched.size() > 5) strengths.add("Strong keyword coverage with " + kw.matched.size() + " matched terms");
        if (sectionScore >= 80) strengths.add("Well-structured resume with all key sections present");
        if (skillsScore >= 70) strengths.add("Skills section aligns well with the target role");
        if (formattingScore >= 70) strengths.add("Professional formatting with proper contact details");
        if (kw.score >= 70) strengths.add("High ATS keyword match rate — likely to pass automated screening");
        if (strengths.isEmpty()) strengths.add("Resume has been successfully parsed and analyzed");
        return strengths;
    }

    private List<String> buildWeaknesses(KeywordMatchResult kw, double sectionScore,
                                          double skillsScore, double formattingScore,
                                          List<ParsedResumeSection> sections) {
        List<String> weaknesses = new ArrayList<>();
        if (kw.missing.size() > 5) weaknesses.add("Missing " + kw.missing.size() + " important keywords for this role");
        if (sectionScore < 60) weaknesses.add("Several key resume sections are missing or too brief");
        if (skillsScore < 50) weaknesses.add("Skills section doesn't align well with job requirements");
        if (formattingScore < 60) weaknesses.add("Resume formatting could be improved for better ATS parsing");
        boolean hasProjects = sections.stream().anyMatch(s -> s.getSectionType() == SectionType.PROJECTS);
        if (!hasProjects) weaknesses.add("No projects section found — add relevant projects to improve score");
        return weaknesses;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String normalizeText(String text) {
        if (text == null) return "";
        return text.toLowerCase().replaceAll("[^a-z0-9\\s+#.]", " ").replaceAll("\\s+", " ").trim();
    }

    private int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner classes
    // ─────────────────────────────────────────────────────────────────────────

    public record KeywordMatchResult(double score, List<String> matched, List<String> missing) {}

    @lombok.Data @lombok.Builder
    public static class AtsScoreResult {
        private double overallScore;
        private double keywordScore;
        private double sectionScore;
        private double formattingScore;
        private double experienceScore;
        private double skillsScore;
        private double readabilityScore;
        private double projectScore;
        private double roleMatchPercentage;
        private List<String> missingKeywords;
        private List<String> matchedKeywords;
        private List<String> strengths;
        private List<String> weaknesses;
    }
}
