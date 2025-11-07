package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.domain.model.StockCompany;
import com.quant.market.domain.repository.StockCompanyRepository;
import com.quant.market.infrastructure.persistence.entity.StockCompanyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stock Company Repository Implementation
 */
@Component
@RequiredArgsConstructor
public class StockCompanyRepositoryImpl implements StockCompanyRepository {

    private final StockCompanyJpaRepository jpaRepository;

    @Override
    public StockCompany save(StockCompany company) {
        StockCompanyEntity entity = StockCompanyEntity.fromDomain(company);
        StockCompanyEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public List<StockCompany> saveAll(List<StockCompany> companies) {
        List<StockCompanyEntity> entities = companies.stream()
                .map(StockCompanyEntity::fromDomain)
                .collect(Collectors.toList());
        List<StockCompanyEntity> saved = jpaRepository.saveAll(entities);
        return saved.stream()
                .map(StockCompanyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StockCompany> findById(Long id) {
        return jpaRepository.findById(id)
                .map(StockCompanyEntity::toDomain);
    }

    @Override
    public Optional<StockCompany> findByStockCode(String stockCode) {
        return jpaRepository.findByStockCode(stockCode)
                .map(StockCompanyEntity::toDomain);
    }

    @Override
    public Optional<StockCompany> findByStockCodeAndExchange(String stockCode, String exchange) {
        return jpaRepository.findByStockCodeAndExchange(stockCode, exchange)
                .map(StockCompanyEntity::toDomain);
    }

    @Override
    public Optional<StockCompany> findByUniqueFields(
            String stockCode, String comName, String comId, String chairman, String exchange) {
        return jpaRepository.findByStockCodeAndComNameAndComIdAndChairmanAndExchange(
                        stockCode, comName, comId, chairman, exchange)
                .map(StockCompanyEntity::toDomain);
    }

    @Override
    public boolean existsByUniqueFields(
            String stockCode, String comName, String comId, String chairman, String exchange) {
        return jpaRepository.existsByStockCodeAndComNameAndComIdAndChairmanAndExchange(
                stockCode, comName, comId, chairman, exchange);
    }

    @Override
    public List<StockCompany> findAll() {
        return jpaRepository.findAll().stream()
                .map(StockCompanyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockCompany> findByExchange(String exchange) {
        return jpaRepository.findByExchange(exchange).stream()
                .map(StockCompanyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockCompany> findByStockCodeIn(List<String> stockCodes) {
        return jpaRepository.findByStockCodeIn(stockCodes).stream()
                .map(StockCompanyEntity::toDomain)
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
}
