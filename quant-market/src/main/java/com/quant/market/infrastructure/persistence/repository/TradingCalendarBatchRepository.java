package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.infrastructure.persistence.entity.TradingCalendarEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

/**
 * Trading Calendar Batch Repository
 * High-performance batch insert/update using native SQL and JDBC batch operations
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TradingCalendarBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Batch upsert trading calendar data (insert or update)
     * If trade_date exists, update; otherwise insert
     *
     * @param entities List of trading calendar entities to upsert
     * @return Number of rows affected
     */
    @Transactional
    public int batchUpsert(List<TradingCalendarEntity> entities) {
        if (entities.isEmpty()) {
            return 0;
        }

        String sql = """
            INSERT INTO t_trading_calendar (
                trade_date, is_trading_day, created_at, updated_at
            ) VALUES (?, ?, ?, ?)
            ON CONFLICT (trade_date)
            DO UPDATE SET
                is_trading_day = EXCLUDED.is_trading_day,
                updated_at = CURRENT_TIMESTAMP
            """;

        int[][] results = jdbcTemplate.batchUpdate(sql, entities, entities.size(),
            (PreparedStatement ps, TradingCalendarEntity entity) -> {
                Timestamp now = new Timestamp(System.currentTimeMillis());

                ps.setObject(1, entity.getTradeDate());
                ps.setShort(2, entity.getIsTradingDay());
                ps.setTimestamp(3, now);
                ps.setTimestamp(4, now);
            });

        int totalAffected = 0;
        for (int[] result : results) {
            totalAffected += result.length;
        }

        log.info("Batch upsert trading calendar completed: {} rows affected", totalAffected);
        return totalAffected;
    }
}
