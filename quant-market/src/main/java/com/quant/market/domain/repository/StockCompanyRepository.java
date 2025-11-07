package com.quant.market.domain.repository;

import com.quant.market.domain.model.StockCompany;

import java.util.List;
import java.util.Optional;

/**
 * Stock Company Repository Interface
 */
public interface StockCompanyRepository {

    /**
     * Save company
     */
    StockCompany save(StockCompany company);

    /**
     * Batch save companies
     */
    List<StockCompany> saveAll(List<StockCompany> companies);

    /**
     * Find by ID
     */
    Optional<StockCompany> findById(Long id);

    /**
     * Find by stock code
     */
    Optional<StockCompany> findByStockCode(String stockCode);

    /**
     * Find by stock code and exchange
     */
    Optional<StockCompany> findByStockCodeAndExchange(String stockCode, String exchange);

    /**
     * Find by unique constraint fields
     * Used for checking duplicates during batch upsert
     */
    Optional<StockCompany> findByUniqueFields(
            String stockCode, String comName, String comId, String chairman, String exchange);

    /**
     * Check if exists by unique constraint fields
     */
    boolean existsByUniqueFields(
            String stockCode, String comName, String comId, String chairman, String exchange);

    /**
     * Find all companies
     */
    List<StockCompany> findAll();

    /**
     * Find by exchange
     */
    List<StockCompany> findByExchange(String exchange);

    /**
     * Find companies by stock codes (batch query)
     */
    List<StockCompany> findByStockCodeIn(List<String> stockCodes);

    /**
     * Delete company by ID
     */
    void deleteById(Long id);

    /**
     * Count all companies
     */
    long count();
}
