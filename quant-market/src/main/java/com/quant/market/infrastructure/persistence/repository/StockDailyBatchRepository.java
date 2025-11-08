package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.infrastructure.persistence.entity.StockDailyEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

/**
 * Stock Daily Batch Repository
 * High-performance batch insert using native SQL and JDBC batch operations
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class StockDailyBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Batch insert daily data using PostgreSQL ON CONFLICT DO NOTHING
     * This skips duplicates automatically and is much faster than checking existence individually
     *
     * @param entities List of daily entities to insert
     * @return Number of rows inserted (excluding skipped duplicates)
     */
    @Transactional
    public int batchInsert(List<StockDailyEntity> entities) {
        if (entities.isEmpty()) {
            return 0;
        }

        String sql = """
            INSERT INTO t_stock_daily (
                stock_code, trade_date, open_price, high_price, low_price,
                close_price, pre_close, change_amount, pct_change,
                volume, amount, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (stock_code, trade_date) DO NOTHING
            """;

        int[][] results = jdbcTemplate.batchUpdate(sql, entities, entities.size(),
            (PreparedStatement ps, StockDailyEntity entity) -> {
                Timestamp now = new Timestamp(System.currentTimeMillis());

                ps.setString(1, entity.getStockCode());
                ps.setObject(2, entity.getTradeDate());
                ps.setBigDecimal(3, entity.getOpenPrice());
                ps.setBigDecimal(4, entity.getHighPrice());
                ps.setBigDecimal(5, entity.getLowPrice());
                ps.setBigDecimal(6, entity.getClosePrice());
                ps.setBigDecimal(7, entity.getPreClose());
                ps.setBigDecimal(8, entity.getChangeAmount());
                ps.setBigDecimal(9, entity.getPctChange());
                ps.setBigDecimal(10, entity.getVolume());
                ps.setBigDecimal(11, entity.getAmount());
                ps.setTimestamp(12, now);
                ps.setTimestamp(13, now);
            });

        int totalInserted = 0;
        for (int[] result : results) {
            totalInserted += result.length;
        }

        log.info("Batch insert completed: {} rows inserted, {} duplicates skipped",
                totalInserted, entities.size() - totalInserted);
        return totalInserted;
    }

    /**
     * Batch upsert daily data (insert or update)
     * Updates existing records with new values
     *
     * @param entities List of daily entities to upsert
     * @return Number of rows affected
     */
    @Transactional
    public int batchUpsert(List<StockDailyEntity> entities) {
        if (entities.isEmpty()) {
            return 0;
        }

        String sql = """
            INSERT INTO t_stock_daily (
                stock_code, trade_date, open_price, high_price, low_price,
                close_price, pre_close, change_amount, pct_change,
                volume, amount, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (stock_code, trade_date)
            DO UPDATE SET
                open_price = EXCLUDED.open_price,
                high_price = EXCLUDED.high_price,
                low_price = EXCLUDED.low_price,
                close_price = EXCLUDED.close_price,
                pre_close = EXCLUDED.pre_close,
                change_amount = EXCLUDED.change_amount,
                pct_change = EXCLUDED.pct_change,
                volume = EXCLUDED.volume,
                amount = EXCLUDED.amount,
                updated_at = CURRENT_TIMESTAMP
            """;

        int[][] results = jdbcTemplate.batchUpdate(sql, entities, entities.size(),
            (PreparedStatement ps, StockDailyEntity entity) -> {
                Timestamp now = new Timestamp(System.currentTimeMillis());

                ps.setString(1, entity.getStockCode());
                ps.setObject(2, entity.getTradeDate());
                ps.setBigDecimal(3, entity.getOpenPrice());
                ps.setBigDecimal(4, entity.getHighPrice());
                ps.setBigDecimal(5, entity.getLowPrice());
                ps.setBigDecimal(6, entity.getClosePrice());
                ps.setBigDecimal(7, entity.getPreClose());
                ps.setBigDecimal(8, entity.getChangeAmount());
                ps.setBigDecimal(9, entity.getPctChange());
                ps.setBigDecimal(10, entity.getVolume());
                ps.setBigDecimal(11, entity.getAmount());
                ps.setTimestamp(12, now);
                ps.setTimestamp(13, now);
            });

        int totalAffected = 0;
        for (int[] result : results) {
            totalAffected += result.length;
        }

        log.info("Batch upsert completed: {} rows affected", totalAffected);
        return totalAffected;
    }
}
