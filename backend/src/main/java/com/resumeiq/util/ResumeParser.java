package com.resumeiq.util;

import com.resumeiq.enums.SectionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ResumeParser {

    private static final Map<SectionType, List<String>> SECTION_HEADERS = new LinkedHashMap<>();

    static {
        SECTION_HEADERS.put(SectionType.SUMMARY, List.of(
            "summary", "professional summary", "executive summary", "objective",
            "career objective", "profile", "about me", "introduction"
        ));
        SECTION_HEADERS.put(SectionType.SKILLS, List.of(
            "skills", "technical skills", "core skills", "key skills",
            "competencies", "technologies", "tech stack", "expertise"
        ));
        SECTION_HEADERS.put(SectionType.EXPERIENCE, List.of(
            "experience", "work experience", "professional experience",
            "employment history", "work history", "career history"
        ));
        SECTION_HEADERS.put(SectionType.EDUCATION, List.of(
            "education", "educational background", "academic background",
            "qualifications", "degree"
        ));
        SECTION_HEADERS.put(SectionType.PROJECTS, List.of(
            "projects", "personal projects", "academic projects",
            "side projects", "open source", "portfolio"
        ));
        SECTION_HEADERS.put(SectionType.CERTIFICATIONS, List.of(
            "certifications", "certificates", "credentials",
            "professional certifications", "licenses"
        ));
        SECTION_HEADERS.put(SectionType.ACHIEVEMENTS, List.of(
            "achievements", "accomplishments", "awards", "honors", "recognition"
        ));
    }

    public String extractText(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (isPdf(contentType, fileName)) return extractFromPdf(file.getInputStream());
        if (isDocx(contentType, fileName)) return extractFromDocx(file.getInputStream());
        throw new IllegalArgumentException("Unsupported file type. Only PDF and DOCX are supported.");
    }

    private String extractFromPdf(InputStream is) throws IOException {
        try (PDDocument doc = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(doc);
        }
    }

    private String extractFromDocx(InputStream is) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(is)) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph para : doc.getParagraphs()) {
                String text = para.getText().trim();
                if (!text.isEmpty()) sb.append(text).append("\n");
            }
            return sb.toString();
        }
    }

    public Map<SectionType, String> parseSections(String text) {
        Map<SectionType, String> result = new LinkedHashMap<>();
        if (text == null || text.isBlank()) return result;

        String[] lines = text.split("\\r?\\n");
        SectionType currentSection = null;
        StringBuilder currentContent = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            SectionType detected = detectSectionHeader(trimmed);
            if (detected != null) {
                if (currentSection != null && currentContent.length() > 0) {
                    result.merge(currentSection, currentContent.toString().trim(), (a, b) -> a + "\n" + b);
                }
                currentSection = detected;
                currentContent = new StringBuilder();
            } else if (currentSection != null) {
                currentContent.append(trimmed).append("\n");
            }
        }
        if (currentSection != null && currentContent.length() > 0) {
            result.merge(currentSection, currentContent.toString().trim(), (a, b) -> a + "\n" + b);
        }
        return result;
    }

    private SectionType detectSectionHeader(String line) {
        if (line.length() > 60) return null;
        String normalized = line.toLowerCase().replaceAll("[^a-z\\s&]", "").trim();
        for (Map.Entry<SectionType, List<String>> entry : SECTION_HEADERS.entrySet()) {
            for (String header : entry.getValue()) {
                if (normalized.equals(header) || normalized.startsWith(header)) return entry.getKey();
            }
        }
        return null;
    }

    public String extractEmail(String text) {
        Matcher m = Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}").matcher(text);
        return m.find() ? m.group() : null;
    }

    public String extractPhone(String text) {
        Matcher m = Pattern.compile("(\\+\\d{1,3}[\\s.-]?)?(\\(?\\d{3}\\)?[\\s.-]?)(\\d{3}[\\s.-]?\\d{4})").matcher(text);
        return m.find() ? m.group().trim() : null;
    }

    public String extractName(String text) {
        for (String line : text.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.contains("@")) continue;
            if (trimmed.matches(".*\\d{7,}.*")) continue;
            if (trimmed.length() >= 2 && trimmed.length() <= 60) return trimmed;
        }
        return null;
    }

    public String getFileType(MultipartFile file) {
        String ct = file.getContentType();
        String name = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (isPdf(ct, name)) return "PDF";
        if (isDocx(ct, name)) return "DOCX";
        throw new IllegalArgumentException("Unsupported file type");
    }

    public boolean isValidFileType(MultipartFile file) {
        String ct = file.getContentType();
        String name = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        return isPdf(ct, name) || isDocx(ct, name);
    }

    private boolean isPdf(String ct, String name) {
        return "application/pdf".equalsIgnoreCase(ct) || name.endsWith(".pdf");
    }

    private boolean isDocx(String ct, String name) {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(ct)
                || name.endsWith(".docx");
    }

    public int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }
}
