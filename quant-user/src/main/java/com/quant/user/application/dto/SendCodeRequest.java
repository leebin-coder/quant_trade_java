package com.quant.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Send Verification Code Request
 */
@Data
public class SendCodeRequest {

    @NotBlank(message = "Phone number cannot be empty")
    private String phone;
}
