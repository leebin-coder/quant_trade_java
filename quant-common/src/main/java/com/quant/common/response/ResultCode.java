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
    NOT_FOUND_ERROR(1003, "Resource Not Found");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
