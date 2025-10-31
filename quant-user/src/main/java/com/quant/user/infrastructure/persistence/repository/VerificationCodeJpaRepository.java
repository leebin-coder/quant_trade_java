package com.quant.user.infrastructure.persistence.repository;

import com.quant.user.infrastructure.persistence.entity.VerificationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Verification Code JPA Repository
 */
@Repository
public interface VerificationCodeJpaRepository extends JpaRepository<VerificationCodeEntity, Long> {

    /**
     * Find the latest valid verification code for a phone number
     */
    Optional<VerificationCodeEntity> findFirstByPhoneAndIsUsedFalseAndExpiredAtAfterOrderByCreatedAtDesc(
            String phone, LocalDateTime now);

    /**
     * Delete expired verification codes
     */
    void deleteByExpiredAtBefore(LocalDateTime time);
}
