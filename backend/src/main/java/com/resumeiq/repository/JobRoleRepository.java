package com.resumeiq.repository;

import com.resumeiq.entity.JobRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRoleRepository extends JpaRepository<JobRole, Long> {
    List<JobRole> findByIsActiveTrue();
    Optional<JobRole> findBySlug(String slug);
    Page<JobRole> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsBySlug(String slug);
}
