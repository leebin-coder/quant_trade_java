package com.quant.user.infrastructure.persistence.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Verification Code JPA Entity
 */
@Data
@Entity
@Table(name = "verification_codes")
public class VerificationCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CodeType type;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    public enum CodeType {
        LOGIN,
        REGISTER,
        RESET_PASSWORD
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Check if the code is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    /**
     * Check if the code is valid (not used and not expired)
     */
    public boolean isValid() {
        return !isUsed && !isExpired();
    }

    /**
     * Mark the code as used
     */
    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }
}
