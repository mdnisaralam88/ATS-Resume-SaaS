package com.resumeiq.service;

import com.resumeiq.dto.request.*;
import com.resumeiq.dto.response.AuthResponse;
import com.resumeiq.dto.response.UserResponse;
import com.resumeiq.entity.RefreshToken;
import com.resumeiq.entity.Subscription;
import com.resumeiq.entity.User;
import com.resumeiq.enums.NotificationType;
import com.resumeiq.enums.Role;
import com.resumeiq.enums.SubscriptionPlan;
import com.resumeiq.exception.BadRequestException;
import com.resumeiq.exception.ResourceNotFoundException;
import com.resumeiq.exception.UnauthorizedException;
import com.resumeiq.repository.RefreshTokenRepository;
import com.resumeiq.repository.SubscriptionRepository;
import com.resumeiq.repository.UserRepository;
import com.resumeiq.security.JwtTokenProvider;
import com.resumeiq.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final NotificationService notificationService;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${app.email-verification-expiry:86400000}")
    private long emailVerificationExpiry;

    @Value("${app.password-reset-expiry:3600000}")
    private long passwordResetExpiry;

    // ── Register ─────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists.");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .subscriptionPlan(SubscriptionPlan.FREE)
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .emailVerificationExpiry(LocalDateTime.now().plusSeconds(emailVerificationExpiry / 1000))
                .active(true)
                .build();

        user = userRepository.save(user);

        // Create default FREE subscription
        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(SubscriptionPlan.FREE)
                .status("ACTIVE")
                .scansLimit(2)
                .scansUsedToday(0)
                .totalScansUsed(0)
                .startDate(LocalDate.now())
                .renewalDate(LocalDate.now().plusMonths(1))
                .build();
        subscriptionRepository.save(subscription);

        // Send welcome notification
        notificationService.create(user, NotificationType.EMAIL_VERIFIED,
                "Welcome to ResumeIQ!",
                "Your account has been created. Start by uploading your resume to get your ATS score.",
                "/dashboard");

        log.info("New user registered: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) throw new UnauthorizedException("Account is deactivated.");

        // Revoke old refresh tokens for this user
        refreshTokenRepository.revokeAllByUser(user);

        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    // ── Refresh Token ─────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked()) throw new UnauthorizedException("Refresh token has been revoked");
        if (refreshToken.getExpiryDate().isBefore(Instant.now()))
            throw new UnauthorizedException("Refresh token has expired. Please login again.");

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(mapToUserResponse(user))
                .build();
    }

    // ── Forgot Password ───────────────────────────────────────────────────────

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordResetExpiry(LocalDateTime.now().plusSeconds(passwordResetExpiry / 1000));
            userRepository.save(user);
            // In production: send email with reset link containing the token
            log.info("Password reset token generated for: {} | Token: {}", user.getEmail(), token);
        });
    }

    // ── Reset Password ────────────────────────────────────────────────────────

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (user.getPasswordResetExpiry() == null || LocalDateTime.now().isAfter(user.getPasswordResetExpiry()))
            throw new BadRequestException("Password reset token has expired. Please request a new one.");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);
        log.info("Password reset successful for: {}", user.getEmail());
    }

    // ── Change Password ───────────────────────────────────────────────────────

    @Transactional
    public void changePassword(ChangePasswordRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new BadRequestException("Current password is incorrect");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ── Verify Email ──────────────────────────────────────────────────────────

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));

        if (user.getEmailVerificationExpiry() != null && LocalDateTime.now().isAfter(user.getEmailVerificationExpiry()))
            throw new BadRequestException("Verification token has expired. Please request a new one.");

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiry(null);
        userRepository.save(user);
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    @Transactional
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user ->
                refreshTokenRepository.revokeAllByUser(user));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshTokenStr = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(mapToUserResponse(user))
                .build();
    }

    public UserResponse mapToUserResponse(User user) {
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
}
