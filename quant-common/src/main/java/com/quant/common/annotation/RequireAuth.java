package com.quant.common.annotation;

import java.lang.annotation.*;

/**
 * Annotation to mark methods/controllers that require authentication
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAuth {
    /**
     * Whether authentication is required
     */
    boolean required() default true;
}
