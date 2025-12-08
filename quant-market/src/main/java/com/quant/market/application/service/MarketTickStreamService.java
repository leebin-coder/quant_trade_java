package com.quant.market.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quant.market.application.dto.MarketRealtimeTickDTO;
import com.quant.market.application.dto.TradingCalendarDTO;
import com.quant.market.application.enums.MarketTradingPhase;
import com.quant.market.infrastructure.persistence.repository.MarketRealtimeTickClickHouseRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Handles tick streaming logic for websocket clients.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketTickStreamService {

    private static final LocalTime PRE_OPEN_START = LocalTime.of(9, 15);
    private static final LocalTime PRE_OPEN_END = LocalTime.of(9, 25);
    private static final LocalTime SILENT_START = LocalTime.of(9, 25);
    private static final LocalTime SILENT_END = LocalTime.of(9, 30);
    private static final LocalTime MORNING_START = LocalTime.of(9, 30);
    private static final LocalTime MORNING_END = LocalTime.of(11, 30);
    private static final LocalTime LUNCH_START = LocalTime.of(11, 30);
    private static final LocalTime LUNCH_END = LocalTime.of(13, 0);
    private static final LocalTime AFTERNOON_START = LocalTime.of(13, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(15, 0);

    private final MarketRealtimeTickClickHouseRepository tickRepository;
    private final TradingCalendarService tradingCalendarService;
    private final ThreadPoolTaskScheduler tickStreamScheduler;
    private final ObjectMapper objectMapper;

    private final Map<String, StreamContext> contexts = new ConcurrentHashMap<>();

    /**
     * Start streaming for a websocket session.
     */
    public void startStreaming(WebSocketSession session, String stockCode) {
        StreamContext context = new StreamContext(session, stockCode);
        contexts.put(session.getId(), context);

        tickStreamScheduler.execute(() -> initializeStream(context));
    }

    /**
     * Stop streaming for a websocket session.
     */
    public void stopStreaming(String sessionId) {
        StreamContext context = contexts.remove(sessionId);
        if (context != null) {
            Optional.ofNullable(context.getFuture()).ifPresent(future -> future.cancel(true));
        }
    }

    private void initializeStream(StreamContext context) {
        log.info("Initializing tick stream for sessionId={}, stockCode={}",
                context.getSession().getId(), context.getStockCode());
        try {
            TradingCalendarDTO tradingDay = tradingCalendarService
                    .getLatestTradingDayOnOrBefore(LocalDate.now());
            context.setTradingDate(tradingDay.getTradeDate());

            List<MarketRealtimeTickDTO> ticks = tickRepository
                    .findTicksForDate(context.getStockCode(), context.getTradingDate());
            context.setLastTickTime(ticks.isEmpty() ? null : ticks.get(ticks.size() - 1).getTime());

            MarketTradingPhase phase = determinePhase(context.getTradingDate(), LocalDateTime.now());
            context.setPhase(phase);

            sendMessage(context.getSession(), TickStreamMessage.builder()
                    .type("INITIAL")
                    .phase(phase)
                    .message(initialMessage(phase, context.isTradingToday()))
                    .stockCode(context.getStockCode())
                    .tradingDate(context.getTradingDate())
                    .ticks(ticks)
                    .tradingFinished(isFinished(context))
                    .build());

            if (isFinished(context)) {
                closeSession(context, "Trading finished");
                return;
            }

            schedulePolling(context);
        } catch (Exception ex) {
            log.error("Failed to initialize tick stream", ex);
            closeWithError(context, "Failed to initialize tick stream: " + ex.getMessage());
        }
    }

    private void schedulePolling(StreamContext context) {
        ScheduledFuture<?> future = tickStreamScheduler.scheduleAtFixedRate(
                () -> pollLatestTick(context),
                Duration.ofSeconds(3));
        context.setFuture(future);
    }

    private void pollLatestTick(StreamContext context) {
        try {
            WebSocketSession session = context.getSession();
            if (!session.isOpen()) {
                stopStreaming(session.getId());
                return;
            }

            MarketTradingPhase newPhase = determinePhase(context.getTradingDate(), LocalDateTime.now());
            if (newPhase != context.getPhase()) {
                context.setPhase(newPhase);
                log.info("Phase changed for sessionId={}, stockCode={}, newPhase={}",
                        session.getId(), context.getStockCode(), newPhase);
                sendMessage(session, TickStreamMessage.builder()
                        .type("STATE")
                        .phase(newPhase)
                        .message(initialMessage(newPhase, context.isTradingToday()))
                        .stockCode(context.getStockCode())
                        .tradingDate(context.getTradingDate())
                        .tradingFinished(isFinished(context))
                        .build());

                if (newPhase == MarketTradingPhase.FINISHED) {
                    closeSession(context, "Trading finished");
                    return;
                }
            }

            Optional<MarketRealtimeTickDTO> latestTick = tickRepository
                    .findLatestTick(context.getStockCode(), context.getTradingDate());

            if (latestTick.isEmpty()) {
                return;
            }

            if (shouldPush(latestTick.get(), context.getLastTickTime())) {
                context.setLastTickTime(latestTick.get().getTime());
                sendMessage(session, TickStreamMessage.builder()
                        .type("UPDATE")
                        .phase(context.getPhase())
                        .stockCode(context.getStockCode())
                        .tradingDate(context.getTradingDate())
                        .tick(latestTick.get())
                        .tradingFinished(false)
                        .build());
            }
        } catch (Exception ex) {
            log.error("Tick polling error", ex);
            closeWithError(context, "Tick polling interrupted: " + ex.getMessage());
        }
    }

    private boolean shouldPush(MarketRealtimeTickDTO latest, LocalDateTime lastTime) {
        if (latest.getTime() == null) {
            return false;
        }
        if (lastTime == null) {
            return true;
        }
        long deltaSeconds = Duration.between(lastTime, latest.getTime()).toSeconds();
        return deltaSeconds >= 3;
    }

    private boolean isFinished(StreamContext context) {
        if (!context.isTradingToday()) {
            return true;
        }
        return context.getPhase() == MarketTradingPhase.FINISHED;
    }

    private MarketTradingPhase determinePhase(LocalDate tradingDate, LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        if (tradingDate.isBefore(today)) {
            return MarketTradingPhase.FINISHED;
        }
        if (tradingDate.isAfter(today)) {
            return MarketTradingPhase.WAITING;
        }

        LocalTime time = now.toLocalTime();
        if (time.isBefore(PRE_OPEN_START)) {
            return MarketTradingPhase.WAITING;
        }
        if (!time.isBefore(PRE_OPEN_START) && time.isBefore(PRE_OPEN_END)) {
            return MarketTradingPhase.TRADING;
        }
        if (!time.isBefore(SILENT_START) && time.isBefore(SILENT_END)) {
            return MarketTradingPhase.WAITING;
        }
        if (!time.isBefore(MORNING_START) && time.isBefore(MORNING_END)) {
            return MarketTradingPhase.TRADING;
        }
        if (!time.isBefore(LUNCH_START) && time.isBefore(LUNCH_END)) {
            return MarketTradingPhase.WAITING;
        }
        if (!time.isBefore(AFTERNOON_START) && time.isBefore(AFTERNOON_END)) {
            return MarketTradingPhase.TRADING;
        }
        return MarketTradingPhase.FINISHED;
    }

    private String initialMessage(MarketTradingPhase phase, boolean isToday) {
        return switch (phase) {
            case TRADING -> "实时连接，持续推送行情";
            case WAITING -> isToday
                    ? "等待交易时段开始，稍后将继续推送"
                    : "已返回历史数据，等待下一交易日";
            case FINISHED -> "今日交易已结束，可断开连接";
        };
    }

    private void sendMessage(WebSocketSession session, TickStreamMessage payload) throws JsonProcessingException {
        if (!session.isOpen()) {
            log.warn("Skip sending message, session {} already closed", session.getId());
            return;
        }
        String json = objectMapper.writeValueAsString(payload);
        try {
            session.sendMessage(new TextMessage(json));
            log.debug("Sent {} message to session {}", payload.getType(), session.getId());
        } catch (IOException e) {
            log.error("Failed to send websocket message", e);
        }
    }

    private void closeSession(StreamContext context, String reason) {
        log.info("Closing session {} for stockCode {} reason {}",
                context.getSession().getId(), context.getStockCode(), reason);
        stopStreaming(context.getSession().getId());
        try {
            if (context.getSession().isOpen()) {
                context.getSession().close(new CloseStatus(1000, reason));
            }
        } catch (IOException e) {
            log.warn("Failed to close websocket session: {}", e.getMessage());
        }
    }

    private void closeWithError(StreamContext context, String reason) {
        try {
            log.warn("Closing session {} due to error: {}", context.getSession().getId(), reason);
            sendMessage(context.getSession(), TickStreamMessage.builder()
                    .type("ERROR")
                    .phase(MarketTradingPhase.FINISHED)
                    .message(reason)
                    .stockCode(context.getStockCode())
                    .tradingDate(context.getTradingDate())
                    .tradingFinished(true)
                    .build());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize error message", e);
        }
        closeSession(context, reason);
    }

    @Data
    private static class StreamContext {
        private final WebSocketSession session;
        private final String stockCode;
        private LocalDate tradingDate;
        private LocalDateTime lastTickTime;
        private MarketTradingPhase phase = MarketTradingPhase.WAITING;
        private ScheduledFuture<?> future;

        StreamContext(WebSocketSession session, String stockCode) {
            this.session = session;
            this.stockCode = stockCode;
        }

        boolean isTradingToday() {
            return tradingDate != null && Objects.equals(LocalDate.now(), tradingDate);
        }
    }

    @Data
    private static class TickStreamMessage {
        private String type;
        private MarketTradingPhase phase;
        private String message;
        private String stockCode;
        private LocalDate tradingDate;
        private boolean tradingFinished;
        private List<MarketRealtimeTickDTO> ticks;
        private MarketRealtimeTickDTO tick;

        public static Builder builder() {
            return new Builder();
        }

        static class Builder {
            private final TickStreamMessage message = new TickStreamMessage();

            Builder type(String type) {
                message.setType(type);
                return this;
            }

            Builder phase(MarketTradingPhase phase) {
                message.setPhase(phase);
                return this;
            }

            Builder message(String content) {
                message.setMessage(content);
                return this;
            }

            Builder stockCode(String stockCode) {
                message.setStockCode(stockCode);
                return this;
            }

            Builder tradingDate(LocalDate tradingDate) {
                message.setTradingDate(tradingDate);
                return this;
            }

            Builder tradingFinished(boolean finished) {
                message.setTradingFinished(finished);
                return this;
            }

            Builder ticks(List<MarketRealtimeTickDTO> ticks) {
                message.setTicks(ticks);
                return this;
            }

            Builder tick(MarketRealtimeTickDTO tick) {
                message.setTick(tick);
                return this;
            }

            TickStreamMessage build() {
                return message;
            }
        }
    }
}
