package com.resumeiq.service;

import com.resumeiq.dto.request.CreateJobRoleRequest;
import com.resumeiq.dto.response.JobRoleResponse;
import com.resumeiq.entity.JobRole;
import com.resumeiq.entity.RoleKeyword;
import com.resumeiq.exception.BadRequestException;
import com.resumeiq.exception.ResourceNotFoundException;
import com.resumeiq.repository.JobRoleRepository;
import com.resumeiq.repository.RoleKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobRoleService {

    private final JobRoleRepository jobRoleRepository;
    private final RoleKeywordRepository keywordRepository;

    public List<JobRoleResponse> getAllActive() {
        return jobRoleRepository.findByIsActiveTrue()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public JobRoleResponse getById(Long id) {
        JobRole role = findById(id);
        role.setKeywords(keywordRepository.findByJobRoleId(id));
        return mapToResponse(role);
    }

    @Transactional
    public JobRoleResponse create(CreateJobRoleRequest request) {
        String slug = request.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        if (jobRoleRepository.existsBySlug(slug))
            throw new BadRequestException("A job role with this name already exists.");

        JobRole role = JobRole.builder()
                .name(request.getName()).slug(slug)
                .description(request.getDescription())
                .experienceLevel(request.getExperienceLevel())
                .category(request.getCategory())
                .iconName(request.getIconName())
                .minExperienceYears(request.getMinExperienceYears())
                .maxExperienceYears(request.getMaxExperienceYears())
                .isActive(true).build();
        role = jobRoleRepository.save(role);

        if (request.getKeywords() != null) {
            final JobRole savedRole = role;
            List<RoleKeyword> keywords = request.getKeywords().stream()
                    .map(k -> RoleKeyword.builder()
                            .jobRole(savedRole).keyword(k.getKeyword())
                            .type(k.getType()).weight(k.getWeight() != null ? k.getWeight() : 1)
                            .category(k.getCategory()).build())
                    .collect(Collectors.toList());
            keywordRepository.saveAll(keywords);
            role.setKeywords(keywords);
        }
        return mapToResponse(role);
    }

    @Transactional
    public JobRoleResponse update(Long id, CreateJobRoleRequest request) {
        JobRole role = findById(id);
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setExperienceLevel(request.getExperienceLevel());
        role.setCategory(request.getCategory());
        role.setIconName(request.getIconName());
        role.setMinExperienceYears(request.getMinExperienceYears());
        role.setMaxExperienceYears(request.getMaxExperienceYears());

        if (request.getKeywords() != null) {
            keywordRepository.deleteByJobRoleId(id);
            final JobRole finalRole = role;
            List<RoleKeyword> keywords = request.getKeywords().stream()
                    .map(k -> RoleKeyword.builder()
                            .jobRole(finalRole).keyword(k.getKeyword())
                            .type(k.getType()).weight(k.getWeight() != null ? k.getWeight() : 1)
                            .category(k.getCategory()).build())
                    .collect(Collectors.toList());
            keywordRepository.saveAll(keywords);
            role.setKeywords(keywords);
        }
        return mapToResponse(jobRoleRepository.save(role));
    }

    @Transactional
    public void delete(Long id) {
        JobRole role = findById(id);
        role.setActive(false);
        jobRoleRepository.save(role);
    }

    public JobRole findById(Long id) {
        return jobRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Role", id));
    }

    public JobRoleResponse mapToResponse(JobRole role) {
        List<JobRoleResponse.KeywordResponse> keywords = role.getKeywords() == null ? List.of() :
                role.getKeywords().stream()
                        .map(k -> JobRoleResponse.KeywordResponse.builder()
                                .id(k.getId()).keyword(k.getKeyword()).type(k.getType())
                                .weight(k.getWeight()).category(k.getCategory()).build())
                        .collect(Collectors.toList());
        return JobRoleResponse.builder()
                .id(role.getId()).name(role.getName()).slug(role.getSlug())
                .description(role.getDescription()).experienceLevel(role.getExperienceLevel())
                .category(role.getCategory()).iconName(role.getIconName())
                .isActive(role.isActive()).minExperienceYears(role.getMinExperienceYears())
                .maxExperienceYears(role.getMaxExperienceYears()).keywords(keywords)
                .createdAt(role.getCreatedAt()).build();
    }
}
