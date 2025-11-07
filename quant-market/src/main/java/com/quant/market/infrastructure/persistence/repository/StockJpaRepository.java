package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.domain.model.Stock;
import com.quant.market.infrastructure.persistence.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Stock JPA Repository
 */
@Repository
public interface StockJpaRepository extends JpaRepository<StockEntity, Long>, JpaSpecificationExecutor<StockEntity> {

    Optional<StockEntity> findByExchangeAndStockCode(Stock.Exchange exchange, String stockCode);

    List<StockEntity> findByExchange(Stock.Exchange exchange);

    List<StockEntity> findByStatus(Stock.StockStatus status);

    List<StockEntity> findByStockNameContaining(String stockName);

    List<StockEntity> findByIndustry(String industry);

    boolean existsByExchangeAndStockCode(Stock.Exchange exchange, String stockCode);

    long countByExchange(Stock.Exchange exchange);
}
