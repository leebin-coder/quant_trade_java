package com.quant.common.util;

import java.util.regex.Pattern;

/**
 * Phone Number Validator
 */
public class PhoneValidator {

    // Chinese mobile phone number pattern
    private static final Pattern CHINA_PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * Validate if the phone number is valid
     */
    public static boolean isValid(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return CHINA_PHONE_PATTERN.matcher(phone).matches();
    }
}
