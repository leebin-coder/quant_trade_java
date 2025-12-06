package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.application.dto.MarketRealtimeTickDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ClickHouse repository for realtime tick data.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MarketRealtimeTickClickHouseRepository {

    private final @Qualifier("clickHouseJdbcTemplate") JdbcTemplate jdbcTemplate;

    private static final String BASE_COLUMNS = String.join(", ",
            "ts_code", "name", "trade", "price", "open", "high", "low", "pre_close", "bid", "ask",
            "volume", "amount", "b1_v", "b1_p", "b2_v", "b2_p", "b3_v", "b3_p", "b4_v", "b4_p", "b5_v", "b5_p",
            "a1_v", "a1_p", "a2_v", "a2_p", "a3_v", "a3_p", "a4_v", "a4_p", "a5_v", "a5_p",
            "date", "time", "source", "raw_json");

    private static final RowMapper<MarketRealtimeTickDTO> ROW_MAPPER = (rs, rowNum) ->
            MarketRealtimeTickDTO.builder()
                    .tsCode(rs.getString("ts_code"))
                    .name(rs.getString("name"))
                    .trade(getDouble(rs, "trade"))
                    .price(getDouble(rs, "price"))
                    .open(getDouble(rs, "open"))
                    .high(getDouble(rs, "high"))
                    .low(getDouble(rs, "low"))
                    .preClose(getDouble(rs, "pre_close"))
                    .bid(getDouble(rs, "bid"))
                    .ask(getDouble(rs, "ask"))
                    .volume(getDouble(rs, "volume"))
                    .amount(getDouble(rs, "amount"))
                    .b1V(getDouble(rs, "b1_v"))
                    .b1P(getDouble(rs, "b1_p"))
                    .b2V(getDouble(rs, "b2_v"))
                    .b2P(getDouble(rs, "b2_p"))
                    .b3V(getDouble(rs, "b3_v"))
                    .b3P(getDouble(rs, "b3_p"))
                    .b4V(getDouble(rs, "b4_v"))
                    .b4P(getDouble(rs, "b4_p"))
                    .b5V(getDouble(rs, "b5_v"))
                    .b5P(getDouble(rs, "b5_p"))
                    .a1V(getDouble(rs, "a1_v"))
                    .a1P(getDouble(rs, "a1_p"))
                    .a2V(getDouble(rs, "a2_v"))
                    .a2P(getDouble(rs, "a2_p"))
                    .a3V(getDouble(rs, "a3_v"))
                    .a3P(getDouble(rs, "a3_p"))
                    .a4V(getDouble(rs, "a4_v"))
                    .a4P(getDouble(rs, "a4_p"))
                    .a5V(getDouble(rs, "a5_v"))
                    .a5P(getDouble(rs, "a5_p"))
                    .date(rs.getObject("date", LocalDate.class))
                    .time(toLocalDateTime(rs, "time"))
                    .source(rs.getString("source"))
                    .rawJson(rs.getString("raw_json"))
                    .build();

    /**
     * Query ordered ticks for a trading date.
     */
    public List<MarketRealtimeTickDTO> findTicksForDate(String tsCode, LocalDate tradingDate) {
        log.debug("Query ticks for {} at {}", tsCode, tradingDate);
        String sql = """
                SELECT %s
                FROM quant_trade.market_realtime_ticks
                WHERE ts_code = ?
                  AND date = toDate(?)
                ORDER BY time ASC
                """.formatted(BASE_COLUMNS);
        return jdbcTemplate.query(sql, new Object[]{tsCode, tradingDate.toString()}, ROW_MAPPER);
    }

    /**
     * Fetch the latest tick (by time) for the trading date.
     */
    public Optional<MarketRealtimeTickDTO> findLatestTick(String tsCode, LocalDate tradingDate) {
        String sql = """
                SELECT %s
                FROM quant_trade.market_realtime_ticks
                WHERE ts_code = ?
                  AND date = toDate(?)
                ORDER BY time DESC
                LIMIT 1
                """.formatted(BASE_COLUMNS);
        List<MarketRealtimeTickDTO> ticks = jdbcTemplate.query(sql, new Object[]{tsCode, tradingDate.toString()}, ROW_MAPPER);
        return ticks.isEmpty() ? Optional.empty() : Optional.of(ticks.getFirst());
    }

    private static Double getDouble(ResultSet rs, String column) throws SQLException {
        Object value = rs.getObject(column);
        return value == null ? null : ((Number) value).doubleValue();
    }

    private static LocalDateTime toLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(column);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
