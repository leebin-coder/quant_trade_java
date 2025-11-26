package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.infrastructure.persistence.entity.StockDailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Stock Daily JPA Repository
 */
@Repository
public interface StockDailyJpaRepository extends JpaRepository<StockDailyEntity, Long> {

    /**
     * Find by stock code and trade date (any adjust flag)
     */
    Optional<StockDailyEntity> findByStockCodeAndTradeDate(String stockCode, LocalDate tradeDate);

    /**
     * Find by stock code, trade date and adjust flag
     */
    Optional<StockDailyEntity> findByStockCodeAndTradeDateAndAdjustFlag(
            String stockCode, LocalDate tradeDate, Short adjustFlag);

    /**
     * Check if exists by stock code and trade date (any adjust flag)
     */
    boolean existsByStockCodeAndTradeDate(String stockCode, LocalDate tradeDate);

    /**
     * Check if exists by stock code, trade date and adjust flag
     */
    boolean existsByStockCodeAndTradeDateAndAdjustFlag(
            String stockCode, LocalDate tradeDate, Short adjustFlag);

    /**
     * Find by stock code ordered by trade date descending
     */
    List<StockDailyEntity> findByStockCodeOrderByTradeDateDesc(String stockCode);

    /**
     * Find by stock code ordered by trade date ascending
     */
    List<StockDailyEntity> findByStockCodeOrderByTradeDateAsc(String stockCode);

    /**
     * Find by stock code and date range (descending)
     */
    @Query("SELECT d FROM StockDailyEntity d WHERE d.stockCode = :stockCode " +
           "AND d.tradeDate >= :startDate AND d.tradeDate <= :endDate " +
           "ORDER BY d.tradeDate DESC")
    List<StockDailyEntity> findByStockCodeAndDateRangeDesc(
            @Param("stockCode") String stockCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find by stock code and date range (ascending)
     */
    @Query("SELECT d FROM StockDailyEntity d WHERE d.stockCode = :stockCode " +
           "AND d.tradeDate >= :startDate AND d.tradeDate <= :endDate " +
           "ORDER BY d.tradeDate ASC")
    List<StockDailyEntity> findByStockCodeAndDateRangeAsc(
            @Param("stockCode") String stockCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find by stock code and start date (descending)
     */
    @Query("SELECT d FROM StockDailyEntity d WHERE d.stockCode = :stockCode " +
           "AND d.tradeDate >= :startDate ORDER BY d.tradeDate DESC")
    List<StockDailyEntity> findByStockCodeAndStartDateDesc(
            @Param("stockCode") String stockCode,
            @Param("startDate") LocalDate startDate);

    /**
     * Find by stock code and start date (ascending)
     */
    @Query("SELECT d FROM StockDailyEntity d WHERE d.stockCode = :stockCode " +
           "AND d.tradeDate >= :startDate ORDER BY d.tradeDate ASC")
    List<StockDailyEntity> findByStockCodeAndStartDateAsc(
            @Param("stockCode") String stockCode,
            @Param("startDate") LocalDate startDate);

    /**
     * Find by stock code and end date (descending)
     */
    @Query("SELECT d FROM StockDailyEntity d WHERE d.stockCode = :stockCode " +
           "AND d.tradeDate <= :endDate ORDER BY d.tradeDate DESC")
    List<StockDailyEntity> findByStockCodeAndEndDateDesc(
            @Param("stockCode") String stockCode,
            @Param("endDate") LocalDate endDate);

    /**
     * Find by stock code and end date (ascending)
     */
    @Query("SELECT d FROM StockDailyEntity d WHERE d.stockCode = :stockCode " +
           "AND d.tradeDate <= :endDate ORDER BY d.tradeDate ASC")
    List<StockDailyEntity> findByStockCodeAndEndDateAsc(
            @Param("stockCode") String stockCode,
            @Param("endDate") LocalDate endDate);

    /**
     * Find the latest trade date in the database
     */
    @Query("SELECT MAX(d.tradeDate) FROM StockDailyEntity d")
    LocalDate findLatestTradeDate();

    /**
     * Find the latest trade date for a specific stock code
     */
    @Query("SELECT MAX(d.tradeDate) FROM StockDailyEntity d WHERE d.stockCode = :stockCode")
    LocalDate findLatestTradeDateByStockCode(@Param("stockCode") String stockCode);

    /**
     * Find the latest trade date for a specific stock code and adjust flag
     */
    @Query("SELECT MAX(d.tradeDate) FROM StockDailyEntity d WHERE d.stockCode = :stockCode AND d.adjustFlag = :adjustFlag")
    LocalDate findLatestTradeDateByStockCodeAndAdjustFlag(
            @Param("stockCode") String stockCode,
            @Param("adjustFlag") Short adjustFlag);
}
