package com.resumeiq.repository;

import com.resumeiq.entity.ParsedResumeSection;
import com.resumeiq.enums.SectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParsedResumeSectionRepository extends JpaRepository<ParsedResumeSection, Long> {
    List<ParsedResumeSection> findByResumeId(Long resumeId);
    Optional<ParsedResumeSection> findByResumeIdAndSectionType(Long resumeId, SectionType sectionType);
    void deleteByResumeId(Long resumeId);
}
