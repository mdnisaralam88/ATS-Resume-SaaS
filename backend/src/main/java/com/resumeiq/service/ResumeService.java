package com.resumeiq.service;

import com.resumeiq.dto.response.PageResponse;
import com.resumeiq.dto.response.ResumeResponse;
import com.resumeiq.entity.ParsedResumeSection;
import com.resumeiq.entity.Resume;
import com.resumeiq.entity.User;
import com.resumeiq.enums.SectionType;
import com.resumeiq.exception.BadRequestException;
import com.resumeiq.exception.ResourceNotFoundException;
import com.resumeiq.repository.AtsScoreRepository;
import com.resumeiq.repository.ParsedResumeSectionRepository;
import com.resumeiq.repository.ResumeRepository;
import com.resumeiq.util.ResumeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ParsedResumeSectionRepository sectionRepository;
    private final AtsScoreRepository atsScoreRepository;
    private final ResumeParser resumeParser;
    private final UserService userService;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    @Value("${file.max-size-bytes:10485760}")
    private long maxFileSize;

    @Transactional
    public ResumeResponse upload(MultipartFile file, String title, String email) throws IOException {
        // Validations
        if (file.isEmpty()) throw new BadRequestException("Please select a file to upload.");
        if (!resumeParser.isValidFileType(file))
            throw new BadRequestException("Only PDF and DOCX files are supported.");
        if (file.getSize() > maxFileSize)
            throw new BadRequestException("File size exceeds 10MB limit.");

        User user = userService.findByEmail(email);
        String fileType = resumeParser.getFileType(file);

        // Save file
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir, "resumes");
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Extract text
        String extractedText = "";
        boolean processed = false;
        String processingError = null;
        try {
            extractedText = resumeParser.extractText(file);
            processed = true;
        } catch (Exception e) {
            log.error("Failed to extract text from resume: {}", e.getMessage());
            processingError = e.getMessage();
        }

        // Save resume entity
        Resume resume = Resume.builder()
                .user(user)
                .originalFileName(file.getOriginalFilename())
                .fileType(fileType)
                .fileSize(file.getSize())
                .filePath(filePath.toString())
                .extractedText(extractedText)
                .title(title != null ? title : file.getOriginalFilename())
                .processed(processed)
                .processingError(processingError)
                .build();

        if (processed && !extractedText.isBlank()) {
            resume.setCandidateName(resumeParser.extractName(extractedText));
            resume.setCandidateEmail(resumeParser.extractEmail(extractedText));
            resume.setCandidatePhone(resumeParser.extractPhone(extractedText));
        }

        resume = resumeRepository.save(resume);

        // Parse and save sections
        if (processed) {
            Map<SectionType, String> sections = resumeParser.parseSections(extractedText);
            List<ParsedResumeSection> sectionEntities = new ArrayList<>();
            for (Map.Entry<SectionType, String> entry : sections.entrySet()) {
                sectionEntities.add(ParsedResumeSection.builder()
                        .resume(resume)
                        .sectionType(entry.getKey())
                        .content(entry.getValue())
                        .wordCount(resumeParser.countWords(entry.getValue()))
                        .build());
            }
            sectionRepository.saveAll(sectionEntities);
            resume.setSections(sectionEntities);
        }

        log.info("Resume uploaded successfully: {} for user: {}", resume.getId(), email);
        return mapToResponse(resume);
    }

    public PageResponse<ResumeResponse> getMyResumes(String email, int page, int size) {
        User user = userService.findByEmail(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt").descending());
        Page<Resume> resumes = resumeRepository.findByUserId(user.getId(), pageable);
        return PageResponse.<ResumeResponse>builder()
                .content(resumes.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(resumes.getNumber()).size(resumes.getSize())
                .totalElements(resumes.getTotalElements()).totalPages(resumes.getTotalPages())
                .first(resumes.isFirst()).last(resumes.isLast()).empty(resumes.isEmpty())
                .build();
    }

    public ResumeResponse getResumeById(Long id, String email) {
        User user = userService.findByEmail(email);
        Resume resume = resumeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume", id));
        List<ParsedResumeSection> sections = sectionRepository.findByResumeId(id);
        resume.setSections(sections);
        return mapToResponse(resume);
    }

    @Transactional
    public void deleteResume(Long id, String email) {
        User user = userService.findByEmail(email);
        Resume resume = resumeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume", id));
        try { Files.deleteIfExists(Paths.get(resume.getFilePath())); } catch (IOException e) { /* ignore */ }
        resumeRepository.delete(resume);
    }

    public Resume findById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", id));
    }

    public ResumeResponse mapToResponse(Resume r) {
        List<ResumeResponse.SectionResponse> sections = r.getSections() == null ? List.of() :
                r.getSections().stream().map(s -> ResumeResponse.SectionResponse.builder()
                        .id(s.getId()).sectionType(s.getSectionType().name())
                        .content(s.getContent()).wordCount(s.getWordCount()).qualityScore(s.getQualityScore())
                        .build()).collect(Collectors.toList());

        Double latestScore = atsScoreRepository.findAverageScoreByUserId(r.getUser().getId());

        return ResumeResponse.builder()
                .id(r.getId()).userId(r.getUser().getId())
                .originalFileName(r.getOriginalFileName()).fileType(r.getFileType())
                .fileSize(r.getFileSize()).candidateName(r.getCandidateName())
                .candidateEmail(r.getCandidateEmail()).candidatePhone(r.getCandidatePhone())
                .title(r.getTitle()).processed(r.isProcessed())
                .uploadedAt(r.getUploadedAt()).sections(sections)
                .build();
    }
}
