package com.quant.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Context - holds current user information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private Long userId;
    private String phone;

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    /**
     * Set current user context
     */
    public static void setContext(UserContext context) {
        CONTEXT.set(context);
    }

    /**
     * Get current user context
     */
    public static UserContext getContext() {
        return CONTEXT.get();
    }

    /**
     * Get current user ID
     */
    public static Long getCurrentUserId() {
        UserContext context = getContext();
        return context != null ? context.getUserId() : null;
    }

    /**
     * Get current user phone
     */
    public static String getCurrentUserPhone() {
        UserContext context = getContext();
        return context != null ? context.getPhone() : null;
    }

    /**
     * Clear context
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
