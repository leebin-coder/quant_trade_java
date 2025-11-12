package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.domain.model.StockDaily;
import com.quant.market.domain.repository.StockDailyRepository;
import com.quant.market.infrastructure.persistence.entity.StockDailyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stock Daily Repository Implementation
 */
@Component
@RequiredArgsConstructor
public class StockDailyRepositoryImpl implements StockDailyRepository {

    private final StockDailyJpaRepository jpaRepository;

    @Override
    public StockDaily save(StockDaily daily) {
        StockDailyEntity entity = StockDailyEntity.fromDomain(daily);
        StockDailyEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public List<StockDaily> saveAll(List<StockDaily> dailyList) {
        List<StockDailyEntity> entities = dailyList.stream()
                .map(StockDailyEntity::fromDomain)
                .collect(Collectors.toList());
        List<StockDailyEntity> saved = jpaRepository.saveAll(entities);
        return saved.stream()
                .map(StockDailyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StockDaily> findById(Long id) {
        return jpaRepository.findById(id)
                .map(StockDailyEntity::toDomain);
    }

    @Override
    public Optional<StockDaily> findByStockCodeAndTradeDate(String stockCode, LocalDate tradeDate) {
        return jpaRepository.findByStockCodeAndTradeDate(stockCode, tradeDate)
                .map(StockDailyEntity::toDomain);
    }

    @Override
    public boolean existsByStockCodeAndTradeDate(String stockCode, LocalDate tradeDate) {
        return jpaRepository.existsByStockCodeAndTradeDate(stockCode, tradeDate);
    }

    @Override
    public List<StockDaily> queryDailyData(
            String stockCode,
            LocalDate startDate,
            LocalDate endDate,
            boolean ascending) {

        List<StockDailyEntity> entities;

        // Case 1: Both startDate and endDate provided
        if (startDate != null && endDate != null) {
            entities = ascending
                    ? jpaRepository.findByStockCodeAndDateRangeAsc(stockCode, startDate, endDate)
                    : jpaRepository.findByStockCodeAndDateRangeDesc(stockCode, startDate, endDate);
        }
        // Case 2: Only startDate provided
        else if (startDate != null) {
            entities = ascending
                    ? jpaRepository.findByStockCodeAndStartDateAsc(stockCode, startDate)
                    : jpaRepository.findByStockCodeAndStartDateDesc(stockCode, startDate);
        }
        // Case 3: Only endDate provided
        else if (endDate != null) {
            entities = ascending
                    ? jpaRepository.findByStockCodeAndEndDateAsc(stockCode, endDate)
                    : jpaRepository.findByStockCodeAndEndDateDesc(stockCode, endDate);
        }
        // Case 4: No date filter
        else {
            entities = ascending
                    ? jpaRepository.findByStockCodeOrderByTradeDateAsc(stockCode)
                    : jpaRepository.findByStockCodeOrderByTradeDateDesc(stockCode);
        }

        return entities.stream()
                .map(StockDailyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByStockCode(String stockCode) {
        return jpaRepository.findByStockCodeOrderByTradeDateDesc(stockCode).size();
    }

    @Override
    public LocalDate findLatestTradeDate() {
        return jpaRepository.findLatestTradeDate();
    }
}
