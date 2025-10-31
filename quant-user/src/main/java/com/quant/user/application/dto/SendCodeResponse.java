package com.quant.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Send Verification Code Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCodeResponse {

    /**
     * Whether the user is registered
     */
    private Boolean isRegistered;

    /**
     * Verification code (for v1, return directly)
     */
    private String code;

    /**
     * Message
     */
    private String message;
}
