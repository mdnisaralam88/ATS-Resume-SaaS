package com.resumeiq.controller;

import com.resumeiq.dto.request.CreateJobRoleRequest;
import com.resumeiq.dto.response.ApiResponse;
import com.resumeiq.dto.response.JobRoleResponse;
import com.resumeiq.service.JobRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Job Roles", description = "Job role templates and keyword management")
public class JobRoleController {

    private final JobRoleService jobRoleService;

    @GetMapping
    @Operation(summary = "Get all active job roles (public)")
    public ResponseEntity<ApiResponse<List<JobRoleResponse>>> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.success(jobRoleService.getAllActive()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job role by ID with keywords")
    public ResponseEntity<ApiResponse<JobRoleResponse>> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(jobRoleService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new job role template (Admin only)")
    public ResponseEntity<ApiResponse<JobRoleResponse>> createRole(
            @Valid @RequestBody CreateJobRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(jobRoleService.create(request), "Job role created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update job role (Admin only)")
    public ResponseEntity<ApiResponse<JobRoleResponse>> updateRole(
            @PathVariable Long id, @Valid @RequestBody CreateJobRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(jobRoleService.update(id, request), "Job role updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a job role (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        jobRoleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Job role deactivated"));
    }
}
