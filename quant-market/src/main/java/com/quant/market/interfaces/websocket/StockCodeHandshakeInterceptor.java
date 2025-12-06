package com.quant.market.interfaces.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * Extracts stockCode from websocket query string.
 */
@Slf4j
public class StockCodeHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        URI uri = request.getURI();
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return false;
        }
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2 && "stockCode".equalsIgnoreCase(parts[0])) {
                String stockCode = parts[1].trim();
                if (!stockCode.isEmpty()) {
                    attributes.put("stockCode", stockCode);
                    return true;
                }
            }
        }
        log.warn("Missing stockCode in websocket handshake: {}", uri);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }
}
