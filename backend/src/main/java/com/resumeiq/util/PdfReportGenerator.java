package com.resumeiq.util;

import com.resumeiq.entity.AtsScore;
import com.resumeiq.entity.ScoreBreakdown;
import com.resumeiq.entity.Suggestion;
import com.resumeiq.enums.SuggestionCategory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class PdfReportGenerator {

    @Value("${file.report-dir:./reports}")
    private String reportDir;

    private static final float MARGIN = 50f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

    public String generateReport(AtsScore atsScore) throws IOException {
        File dir = new File(reportDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "ats-report-" + atsScore.getId() + "-" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
        String filePath = reportDir + File.separator + fileName;

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                // ── Header bar ──────────────────────────────────────────────
                cs.setNonStrokingColor(0.11f, 0.09f, 0.22f); // dark purple
                cs.addRect(0, PAGE_HEIGHT - 80, PAGE_WIDTH, 80);
                cs.fill();

                // Title
                cs.setNonStrokingColor(1f, 1f, 1f);
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 22);
                cs.newLineAtOffset(MARGIN, PAGE_HEIGHT - 45);
                cs.showText("ResumeIQ — ATS Analysis Report");
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                cs.newLineAtOffset(MARGIN, PAGE_HEIGHT - 65);
                cs.showText("Generated: " + atsScore.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                cs.endText();

                y = PAGE_HEIGHT - 100;

                // ── Resume & Role info ───────────────────────────────────────
                cs.setNonStrokingColor(0.2f, 0.2f, 0.2f);
                y = drawText(cs, "Resume: " + atsScore.getResume().getOriginalFileName(),
                        MARGIN, y, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 11);
                y -= 4;
                y = drawText(cs, "Target Role: " + atsScore.getJobRole().getName(),
                        MARGIN, y, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                y -= 15;

                // ── Overall Score box ────────────────────────────────────────
                String grade = getGrade(atsScore.getOverallScore());
                float scoreBoxY = y - 60;
                cs.setNonStrokingColor(0.95f, 0.95f, 1.0f);
                cs.addRect(MARGIN, scoreBoxY, CONTENT_WIDTH, 70);
                cs.fill();

                cs.setNonStrokingColor(0.3f, 0.0f, 0.8f);
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 36);
                cs.newLineAtOffset(MARGIN + 20, scoreBoxY + 25);
                cs.showText(String.format("%.0f / 100", atsScore.getOverallScore()));
                cs.endText();

                cs.setNonStrokingColor(0.2f, 0.2f, 0.2f);
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                cs.newLineAtOffset(MARGIN + 200, scoreBoxY + 40);
                cs.showText("ATS Score  |  Grade: " + grade);
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                cs.newLineAtOffset(MARGIN + 200, scoreBoxY + 20);
                cs.showText("Role Match: " + String.format("%.0f%%", atsScore.getRoleMatchPercentage()));
                cs.endText();

                y = scoreBoxY - 20;

                // ── Category Breakdown ───────────────────────────────────────
                y = drawSectionHeader(cs, "Score Breakdown by Category", y);
                List<ScoreBreakdown> breakdowns = atsScore.getBreakdowns();
                if (breakdowns != null) {
                    for (ScoreBreakdown bd : breakdowns) {
                        y -= 5;
                        cs.setNonStrokingColor(0.2f, 0.2f, 0.2f);
                        y = drawText(cs, String.format("%-28s %5.1f / %-5.0f  (%s)",
                                bd.getCategory(), bd.getScore(), bd.getMaxScore(), bd.getGrade()),
                                MARGIN + 10, y, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);

                        // Progress bar
                        float barY = y + 2;
                        float barWidth = (float)(bd.getScore() / bd.getMaxScore()) * 200;
                        cs.setNonStrokingColor(0.85f, 0.85f, 0.95f);
                        cs.addRect(MARGIN + 10, barY, 200, 6);
                        cs.fill();
                        cs.setNonStrokingColor(0.3f, 0.0f, 0.8f);
                        cs.addRect(MARGIN + 10, barY, barWidth, 6);
                        cs.fill();
                        y -= 14;
                    }
                }
                y -= 10;

                // ── Suggestions ───────────────────────────────────────────────
                y = drawSectionHeader(cs, "Improvement Suggestions", y);
                List<Suggestion> suggestions = atsScore.getSuggestions();
                if (suggestions != null) {
                    for (Suggestion s : suggestions) {
                        if (y < 80) break; // Avoid overflow
                        String prefix = s.getCategory() == SuggestionCategory.CRITICAL ? "[CRITICAL] " :
                                        s.getCategory() == SuggestionCategory.RECOMMENDED ? "[RECOMMENDED] " : "[OPTIONAL] ";
                        y -= 5;
                        cs.setNonStrokingColor(
                            s.getCategory() == SuggestionCategory.CRITICAL ? 0.8f : 0.2f,
                            s.getCategory() == SuggestionCategory.CRITICAL ? 0.1f : 0.5f,
                            0.1f);
                        y = drawWrappedText(cs, prefix + s.getText(), MARGIN + 10, y,
                                new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, CONTENT_WIDTH - 20);
                        y -= 4;
                    }
                }

                // ── Footer ────────────────────────────────────────────────────
                cs.setNonStrokingColor(0.5f, 0.5f, 0.5f);
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 8);
                cs.newLineAtOffset(MARGIN, 30);
                cs.showText("ResumeIQ — AI-Powered ATS Resume Analyzer | resumeiq.com | This report is auto-generated.");
                cs.endText();
            }

            doc.save(filePath);
            log.info("Generated ATS report: {}", filePath);
        }
        return filePath;
    }

    private float drawSectionHeader(PDPageContentStream cs, String title, float y) throws IOException {
        y -= 15;
        cs.setNonStrokingColor(0.11f, 0.09f, 0.22f);
        cs.addRect(MARGIN, y - 4, CONTENT_WIDTH, 20);
        cs.fill();
        cs.setNonStrokingColor(1f, 1f, 1f);
        y = drawText(cs, title, MARGIN + 5, y, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 11);
        cs.setNonStrokingColor(0.2f, 0.2f, 0.2f);
        y -= 8;
        return y;
    }

    private float drawText(PDPageContentStream cs, String text, float x, float y,
                            PDType1Font font, float fontSize) throws IOException {
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - (fontSize + 4);
    }

    private float drawWrappedText(PDPageContentStream cs, String text, float x, float y,
                                   PDType1Font font, float fontSize, float maxWidth) throws IOException {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float lineHeight = fontSize + 4;

        for (String word : words) {
            String testLine = line + (line.length() > 0 ? " " : "") + word;
            float width = font.getStringWidth(testLine) / 1000 * fontSize;
            if (width > maxWidth && line.length() > 0) {
                y = drawText(cs, line.toString(), x, y, font, fontSize);
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        if (line.length() > 0) y = drawText(cs, line.toString(), x, y, font, fontSize);
        return y;
    }

    private String getGrade(double score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        return "F";
    }

    public String getFileName(String filePath) {
        return new File(filePath).getName();
    }
}
