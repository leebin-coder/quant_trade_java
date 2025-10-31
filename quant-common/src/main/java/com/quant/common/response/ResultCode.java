package com.quant.common.response;

import lombok.Getter;

/**
 * Result Code Enum
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "Success"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),

    // Business Error Codes (1000-9999)
    BUSINESS_ERROR(1000, "Business Error"),
    VALIDATION_ERROR(1001, "Validation Error"),
    DUPLICATE_ERROR(1002, "Duplicate Error"),
    NOT_FOUND_ERROR(1003, "Resource Not Found"),

    // User Related Error Codes (2000-2099)
    USER_NOT_REGISTERED(2001, "User Not Registered"),
    USER_ALREADY_REGISTERED(2002, "User Already Registered"),
    INVALID_PHONE_FORMAT(2003, "Invalid Phone Format"),
    VERIFICATION_CODE_ERROR(2004, "Invalid or Expired Verification Code"),
    VERIFICATION_CODE_SEND_FAILED(2005, "Failed to Send Verification Code"),
    INVALID_TOKEN(2006, "Invalid Token"),
    TOKEN_EXPIRED(2007, "Token Expired");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
