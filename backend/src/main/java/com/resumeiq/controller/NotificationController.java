package com.resumeiq.controller;

import com.resumeiq.dto.response.ApiResponse;
import com.resumeiq.dto.response.NotificationResponse;
import com.resumeiq.dto.response.PageResponse;
import com.resumeiq.entity.Notification;
import com.resumeiq.repository.NotificationRepository;
import com.resumeiq.service.NotificationService;
import com.resumeiq.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notification management")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByEmail(userDetails.getUsername());
        Page<Notification> notifPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(
                user.getId(), PageRequest.of(page, size));
        List<NotificationResponse> content = notifPage.getContent().stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId()).type(n.getType().name()).title(n.getTitle())
                        .message(n.getMessage()).isRead(n.isRead())
                        .actionUrl(n.getActionUrl()).createdAt(n.getCreatedAt()).build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(PageResponse.<NotificationResponse>builder()
                .content(content).page(notifPage.getNumber()).size(notifPage.getSize())
                .totalElements(notifPage.getTotalElements()).totalPages(notifPage.getTotalPages())
                .first(notifPage.isFirst()).last(notifPage.isLast()).empty(notifPage.isEmpty()).build()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", notificationService.countUnread(user.getId()))));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllRead(@AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllRead(userService.findByEmail(userDetails.getUsername()).getId());
        return ResponseEntity.ok(ApiResponse.success(null, "All marked as read"));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markRead(id, userService.findByEmail(userDetails.getUsername()).getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Marked as read"));
    }
}
