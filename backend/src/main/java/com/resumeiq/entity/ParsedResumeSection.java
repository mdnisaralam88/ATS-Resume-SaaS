package com.resumeiq.entity;
import com.resumeiq.enums.SectionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
/**
 * Individual parsed section from a resume (skills, experience, education, etc).
 */
@Document
@Table(name = "parsed_resume_sections", indexes = {
    @Index(name = "idx_section_resume", columnList = "resume_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParsedResumeSection {
    @Id
    private String id;
    @DBRef(lazy = true)
    private Resume resume;
    @Enumerated(EnumType.STRING)
    private SectionType sectionType;
    private String content;
    @Column
    private Integer wordCount;
    @Column
    private Float qualityScore;  // 0-100 individual section quality
}
