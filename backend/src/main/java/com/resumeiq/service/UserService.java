package com.resumeiq.service;

import com.resumeiq.dto.request.UpdateProfileRequest;
import com.resumeiq.dto.response.PageResponse;
import com.resumeiq.dto.response.UserResponse;
import com.resumeiq.entity.User;
import com.resumeiq.exception.BadRequestException;
import com.resumeiq.exception.ResourceNotFoundException;
import com.resumeiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    public UserResponse getProfile(String email) {
        User user = findByEmail(email);
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request, String email) {
        User user = findByEmail(email);
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setLocation(request.getLocation());
        user.setBio(request.getBio());
        user.setJobTitle(request.getJobTitle());
        user.setCompany(request.getCompany());
        user.setLinkedinUrl(request.getLinkedinUrl());
        user.setGithubUrl(request.getGithubUrl());
        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse uploadProfileImage(MultipartFile file, String email) throws IOException {
        if (file.isEmpty()) throw new BadRequestException("Profile image file is empty");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new BadRequestException("Only image files are allowed");

        String ext = file.getOriginalFilename() != null
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
                : ".jpg";

        String filename = "profile-" + UUID.randomUUID() + ext;
        Path path = Paths.get(uploadDir, "profiles", filename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        User user = findByEmail(email);
        user.setProfileImage("/uploads/profiles/" + filename);
        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteAccount(String email) {
        User user = findByEmail(email);
        user.setActive(false);
        userRepository.save(user);
    }

    // Admin methods
    public PageResponse<UserResponse> getAllUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = (search != null && !search.isBlank())
                ? userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable)
                : userRepository.findAll(pageable);
        return toPageResponse(users);
    }

    public UserResponse getUserById(Long id) {
        return mapToResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id)));
    }

    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setActive(!user.isActive());
        return mapToResponse(userRepository.save(user));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .profileImage(user.getProfileImage())
                .subscriptionPlan(user.getSubscriptionPlan())
                .emailVerified(user.isEmailVerified())
                .active(user.isActive())
                .phone(user.getPhone())
                .location(user.getLocation())
                .bio(user.getBio())
                .jobTitle(user.getJobTitle())
                .company(user.getCompany())
                .linkedinUrl(user.getLinkedinUrl())
                .githubUrl(user.getGithubUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private PageResponse<UserResponse> toPageResponse(Page<User> page) {
        return PageResponse.<UserResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
