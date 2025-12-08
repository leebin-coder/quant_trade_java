package com.quant.market.interfaces.websocket;

import com.quant.market.application.service.MarketTickStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket handler for realtime tick streaming.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarketTickWebSocketHandler extends TextWebSocketHandler {

    private final MarketTickStreamService tickStreamService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Object stockCodeAttr = session.getAttributes().get("stockCode");
        if (stockCodeAttr == null) {
            closeSession(session, CloseStatus.BAD_DATA.withReason("stockCode is required"));
            return;
        }

        String stockCode = stockCodeAttr.toString();
        log.info("WebSocket connected: sessionId={}, stockCode={}, remote={}",
                session.getId(), stockCode, session.getRemoteAddress());
        tickStreamService.startStreaming(session, stockCode);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Protocol is server push only; ignore client payload to keep connection alive.
        log.debug("Ignoring client message: {}", message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        tickStreamService.stopStreaming(session.getId());
        log.info("WebSocket closed: sessionId={}, closeStatus={}, remote={}",
                session.getId(), status, session.getRemoteAddress());
    }

    private void closeSession(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (Exception e) {
            log.warn("Failed to close session {}: {}", session.getId(), e.getMessage());
        }
    }
}
