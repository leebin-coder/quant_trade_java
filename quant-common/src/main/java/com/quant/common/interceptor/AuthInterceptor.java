package com.quant.common.interceptor;

import com.quant.common.exception.BusinessException;
import com.quant.common.response.ResultCode;
import com.quant.common.security.JwtTokenUtil;
import com.quant.common.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Authentication Interceptor
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Get token from header
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // Validate token
        if (!jwtTokenUtil.validateToken(token)) {
            throw new BusinessException(ResultCode.INVALID_TOKEN);
        }

        // Get user info from token
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        String phone = jwtTokenUtil.getPhoneFromToken(token);

        if (userId == null) {
            throw new BusinessException(ResultCode.INVALID_TOKEN);
        }

        // Set user context
        UserContext context = new UserContext(userId, phone);
        UserContext.setContext(context);

        log.debug("User authenticated - userId: {}, phone: {}", userId, phone);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clear user context
        UserContext.clear();
    }
}
