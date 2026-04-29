package com.resumeiq.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
import java.time.Instant;
/**
 * JWT refresh token storage for token rotation.
 */
@Document
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_token", columnList = "token")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {
    @Id
    private String id;
    private String token;
    @DBRef(lazy = true)
    private User user;
    private Instant expiryDate;
    @Builder.Default
    private boolean revoked = false;
    private String deviceInfo;
}
