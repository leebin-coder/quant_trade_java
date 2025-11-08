package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.infrastructure.persistence.entity.StockCompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Stock Company JPA Repository
 */
@Repository
public interface StockCompanyJpaRepository extends JpaRepository<StockCompanyEntity, Long> {

    /**
     * Find by stock code
     */
    Optional<StockCompanyEntity> findByStockCode(String stockCode);

    /**
     * Find by stock code and exchange
     */
    Optional<StockCompanyEntity> findByStockCodeAndExchange(String stockCode, String exchange);

    /**
     * Find by unique constraint fields
     * Used for checking duplicates during batch upsert
     */
    Optional<StockCompanyEntity> findByStockCodeAndComNameAndComIdAndChairmanAndExchange(
            String stockCode, String comName, String comId, String chairman, String exchange);

    /**
     * Check if exists by unique constraint fields
     */
    boolean existsByStockCodeAndComNameAndComIdAndChairmanAndExchange(
            String stockCode, String comName, String comId, String chairman, String exchange);

    /**
     * Find by exchange
     */
    List<StockCompanyEntity> findByExchange(String exchange);

    /**
     * Find companies by stock codes (batch query)
     */
    @Query("SELECT c FROM StockCompanyEntity c WHERE c.stockCode IN :stockCodes")
    List<StockCompanyEntity> findByStockCodeIn(@Param("stockCodes") List<String> stockCodes);
}
