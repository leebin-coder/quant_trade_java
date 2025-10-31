package com.quant.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * User ID
     */
    private Long userId;

    /**
     * Phone number
     */
    private String phone;

    /**
     * Nick name
     */
    private String nickName;

    /**
     * JWT Token
     */
    private String token;

    /**
     * Whether this is a new registration
     */
    private Boolean isNewUser;
}
