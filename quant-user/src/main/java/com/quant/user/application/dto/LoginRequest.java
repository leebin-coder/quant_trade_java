package com.quant.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Login Request
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Phone number cannot be empty")
    private String phone;

    @NotBlank(message = "Verification code cannot be empty")
    private String code;
}
