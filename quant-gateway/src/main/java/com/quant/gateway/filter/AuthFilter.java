package com.quant.gateway.filter;

import com.quant.gateway.config.JwtConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Gateway Authentication Filter
 * Validates JWT token for all requests except whitelisted paths
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtConfig jwtConfig;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Whitelist paths that don't require authentication
     */
    private static final List<String> WHITELIST_PATHS = Arrays.asList(
            "/api/auth/**",              // Authentication endpoints
            "/actuator/**",          // Actuator endpoints
            "/error",                // Error endpoint
            "/favicon.ico"           // Favicon
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        log.debug("Gateway filter - Request path: {}", path);

        // Check if path is in whitelist
        if (isWhitelisted(path)) {
            log.debug("Path is whitelisted: {}", path);
            return chain.filter(exchange);
        }

        // Get token from header
        String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return unauthorized(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // Validate token
        if (!jwtConfig.validateToken(token)) {
            log.warn("Invalid or expired token for path: {}", path);
            return unauthorized(exchange);
        }

        // Get user info from token
        Long userId = jwtConfig.getUserIdFromToken(token);
        String phone = jwtConfig.getPhoneFromToken(token);

        if (userId == null) {
            log.warn("Invalid token - missing userId for path: {}", path);
            return unauthorized(exchange);
        }

        // Add user info to request headers for downstream services
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Phone", phone != null ? phone : "")
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.debug("User authenticated - userId: {}, phone: {}, path: {}", userId, phone, path);
        return chain.filter(mutatedExchange);
    }

    /**
     * Check if the path is whitelisted
     */
    private boolean isWhitelisted(String path) {
        return WHITELIST_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * Return unauthorized response
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\":401,\"message\":\"Unauthorized - Invalid or missing token\",\"timestamp\":"
                + System.currentTimeMillis() + "}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
