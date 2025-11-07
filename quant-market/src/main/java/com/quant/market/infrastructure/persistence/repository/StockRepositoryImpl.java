package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.domain.model.Stock;
import com.quant.market.domain.repository.StockRepository;
import com.quant.market.infrastructure.persistence.entity.StockEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stock Repository Implementation
 */
@Component
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository jpaRepository;

    @Override
    public Stock save(Stock stock) {
        StockEntity entity = StockEntity.fromDomain(stock);
        StockEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public List<Stock> saveAll(List<Stock> stocks) {
        List<StockEntity> entities = stocks.stream()
                .map(StockEntity::fromDomain)
                .collect(Collectors.toList());
        List<StockEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(StockEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Stock> findById(Long id) {
        return jpaRepository.findById(id)
                .map(StockEntity::toDomain);
    }

    @Override
    public Optional<Stock> findByExchangeAndStockCode(Stock.Exchange exchange, String stockCode) {
        return jpaRepository.findByExchangeAndStockCode(exchange, stockCode)
                .map(StockEntity::toDomain);
    }

    @Override
    public List<Stock> findAll() {
        return jpaRepository.findAll().stream()
                .map(StockEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Stock> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(StockEntity::toDomain);
    }

    @Override
    public List<Stock> findByExchange(Stock.Exchange exchange) {
        return jpaRepository.findByExchange(exchange).stream()
                .map(StockEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Stock> findByStatus(Stock.StockStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(StockEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Stock> findByStockNameContaining(String stockName) {
        return jpaRepository.findByStockNameContaining(stockName).stream()
                .map(StockEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Stock> findByIndustry(String industry) {
        return jpaRepository.findByIndustry(industry).stream()
                .map(StockEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByExchangeAndStockCode(Stock.Exchange exchange, String stockCode) {
        return jpaRepository.existsByExchangeAndStockCode(exchange, stockCode);
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
    public long countByExchange(Stock.Exchange exchange) {
        return jpaRepository.countByExchange(exchange);
    }

    @Override
    public List<Stock> queryStocks(
            LocalDate listingDateFrom,
            LocalDate listingDateTo,
            String keyword,
            List<Stock.StockStatus> statuses,
            List<String> industries,
            List<Stock.Exchange> exchanges) {

        Specification<StockEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Listing date range filter
            if (listingDateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("listingDate"), listingDateFrom));
            }
            if (listingDateTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("listingDate"), listingDateTo));
            }

            // Keyword fuzzy search (match stock code or stock name)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                Predicate stockCodeLike = criteriaBuilder.like(root.get("stockCode"), likePattern);
                Predicate stockNameLike = criteriaBuilder.like(root.get("stockName"), likePattern);
                predicates.add(criteriaBuilder.or(stockCodeLike, stockNameLike));
            }

            // Status multi-select filter
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            // Industry multi-select filter
            if (industries != null && !industries.isEmpty()) {
                predicates.add(root.get("industry").in(industries));
            }

            // Exchange multi-select filter
            if (exchanges != null && !exchanges.isEmpty()) {
                predicates.add(root.get("exchange").in(exchanges));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return jpaRepository.findAll(spec).stream()
                .map(StockEntity::toDomain)
                .collect(Collectors.toList());
    }
}
