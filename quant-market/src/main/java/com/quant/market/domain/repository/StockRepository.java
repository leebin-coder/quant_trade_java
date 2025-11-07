package com.quant.market.domain.repository;

import com.quant.market.domain.model.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Stock Repository Interface
 */
public interface StockRepository {

    /**
     * Save stock
     */
    Stock save(Stock stock);

    /**
     * Batch save stocks
     */
    List<Stock> saveAll(List<Stock> stocks);

    /**
     * Find by ID
     */
    Optional<Stock> findById(Long id);

    /**
     * Find by exchange and stock code
     */
    Optional<Stock> findByExchangeAndStockCode(Stock.Exchange exchange, String stockCode);

    /**
     * Find all stocks
     */
    List<Stock> findAll();

    /**
     * Find stocks with pagination
     */
    Page<Stock> findAll(Pageable pageable);

    /**
     * Find by exchange
     */
    List<Stock> findByExchange(Stock.Exchange exchange);

    /**
     * Find by status
     */
    List<Stock> findByStatus(Stock.StockStatus status);

    /**
     * Find by stock name (fuzzy search)
     */
    List<Stock> findByStockNameContaining(String stockName);

    /**
     * Find by industry
     */
    List<Stock> findByIndustry(String industry);

    /**
     * Check if exists by exchange and stock code
     */
    boolean existsByExchangeAndStockCode(Stock.Exchange exchange, String stockCode);

    /**
     * Delete stock by ID
     */
    void deleteById(Long id);

    /**
     * Count all stocks
     */
    long count();

    /**
     * Count by exchange
     */
    long countByExchange(Stock.Exchange exchange);

    /**
     * Query stocks by multiple conditions
     * @param listingDateFrom Listing date start (inclusive)
     * @param listingDateTo Listing date end (inclusive)
     * @param keyword Fuzzy search keyword (match stock code or stock name)
     * @param statuses Status list (multi-select)
     * @param industries Industry list (multi-select)
     * @param exchanges Exchange list (multi-select)
     * @return List of matching stocks
     */
    List<Stock> queryStocks(
            java.time.LocalDate listingDateFrom,
            java.time.LocalDate listingDateTo,
            String keyword,
            List<Stock.StockStatus> statuses,
            List<String> industries,
            List<Stock.Exchange> exchanges
    );
}
