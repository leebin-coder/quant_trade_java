package com.quant.user.application.service;

import com.quant.common.exception.BusinessException;
import com.quant.common.response.ResultCode;
import com.quant.common.security.JwtTokenUtil;
import com.quant.common.util.PhoneValidator;
import com.quant.user.application.dto.LoginRequest;
import com.quant.user.application.dto.LoginResponse;
import com.quant.user.application.dto.SendCodeRequest;
import com.quant.user.application.dto.SendCodeResponse;
import com.quant.user.domain.model.User;
import com.quant.user.domain.repository.UserRepository;
import com.quant.user.infrastructure.persistence.entity.UserEntity;
import com.quant.user.infrastructure.persistence.entity.VerificationCodeEntity;
import com.quant.user.infrastructure.persistence.repository.UserJpaRepository;
import com.quant.user.infrastructure.persistence.repository.VerificationCodeJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * Authentication Service
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private VerificationCodeJpaRepository verificationCodeJpaRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_MINUTES = 5;

    /**
     * Send verification code
     */
    @Transactional
    public SendCodeResponse sendVerificationCode(SendCodeRequest request) {
        String phone = request.getPhone();

        // Validate phone format
        if (!PhoneValidator.isValid(phone)) {
            throw new BusinessException(ResultCode.INVALID_PHONE_FORMAT);
        }

        // Check if user is registered
        Optional<UserEntity> userOpt = userJpaRepository.findByMobile(phone);
        boolean isRegistered = userOpt.isPresent();

        // Generate verification code
        String code = generateVerificationCode();

        // Save verification code to database
        VerificationCodeEntity verificationCode = new VerificationCodeEntity();
        verificationCode.setPhone(phone);
        verificationCode.setCode(code);
        verificationCode.setType(VerificationCodeEntity.CodeType.LOGIN);
        verificationCode.setExpiredAt(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));
        verificationCodeJpaRepository.save(verificationCode);

        log.info("Verification code sent to phone: {}, code: {}, isRegistered: {}", phone, code, isRegistered);

        // V1: Return code directly (in production, send via SMS service)
        return SendCodeResponse.builder()
                .isRegistered(isRegistered)
                .code(code)
                .message("Verification code sent successfully")
                .build();
    }

    /**
     * Login with verification code
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();

        // Validate phone format
        if (!PhoneValidator.isValid(phone)) {
            throw new BusinessException(ResultCode.INVALID_PHONE_FORMAT);
        }

        // Verify code
        VerificationCodeEntity verificationCode = verificationCodeJpaRepository
                .findFirstByPhoneAndIsUsedFalseAndExpiredAtAfterOrderByCreatedAtDesc(phone, LocalDateTime.now())
                .orElseThrow(() -> new BusinessException(ResultCode.VERIFICATION_CODE_ERROR));

        if (!verificationCode.getCode().equals(code)) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_ERROR);
        }

        if (!verificationCode.isValid()) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_ERROR);
        }

        // Mark code as used
        verificationCode.markAsUsed();
        verificationCodeJpaRepository.save(verificationCode);

        // Check if user exists
        Optional<UserEntity> userOpt = userJpaRepository.findByMobile(phone);
        UserEntity user;
        boolean isNewUser;

        if (userOpt.isEmpty()) {
            // Auto register
            user = new UserEntity();
            user.setMobile(phone);
            user.setNickName("User_" + phone.substring(phone.length() - 4));
            user.setSex(UserEntity.Sex.UNKNOWN);
            user.setStatus(UserEntity.UserStatus.ACTIVE);
            user = userJpaRepository.save(user);
            isNewUser = true;
            log.info("New user registered with phone: {}", phone);
        } else {
            user = userOpt.get();
            isNewUser = false;
            log.info("User logged in with phone: {}", phone);
        }

        // Generate JWT token
        String token = jwtTokenUtil.generateToken(user.getId(), phone);

        return LoginResponse.builder()
                .userId(user.getId())
                .phone(user.getMobile())
                .nickName(user.getNickName())
                .token(token)
                .isNewUser(isNewUser)
                .build();
    }

    /**
     * Generate random verification code
     */
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
