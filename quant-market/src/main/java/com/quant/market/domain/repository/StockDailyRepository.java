package com.quant.market.domain.repository;

import com.quant.market.domain.model.StockDaily;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Stock Daily Repository Interface
 */
public interface StockDailyRepository {

    /**
     * Save daily data
     */
    StockDaily save(StockDaily daily);

    /**
     * Batch save daily data
     */
    List<StockDaily> saveAll(List<StockDaily> dailyList);

    /**
     * Find by ID
     */
    Optional<StockDaily> findById(Long id);

    /**
     * Find by stock code and trade date
     */
    Optional<StockDaily> findByStockCodeAndTradeDate(String stockCode, LocalDate tradeDate);

    /**
     * Check if exists by stock code and trade date
     */
    boolean existsByStockCodeAndTradeDate(String stockCode, LocalDate tradeDate);

    /**
     * Query daily data with filters and sorting
     *
     * @param stockCode Stock code (required)
     * @param startDate Start date (optional)
     * @param endDate End date (optional)
     * @param adjustFlag Adjust flag (optional, null means all types)
     * @param ascending Sort order (true=ascending, false=descending)
     * @return List of daily data
     */
    List<StockDaily> queryDailyData(
            String stockCode,
            LocalDate startDate,
            LocalDate endDate,
            Integer adjustFlag,
            boolean ascending);

    /**
     * Delete by ID
     */
    void deleteById(Long id);

    /**
     * Count all records
     */
    long count();

    /**
     * Count by stock code
     */
    long countByStockCode(String stockCode);

    /**
     * Find the latest trade date in the database
     *
     * @return Latest trade date, or null if no data exists
     */
    LocalDate findLatestTradeDate();

    /**
     * Find the latest trade date for a specific stock and adjust flag
     *
     * @param stockCode Stock code (required)
     * @param adjustFlag Adjust flag (optional, null means ignore adjust flag)
     * @return Latest trade date, or null if no data exists
     */
    LocalDate findLatestTradeDate(String stockCode, Integer adjustFlag);
}
