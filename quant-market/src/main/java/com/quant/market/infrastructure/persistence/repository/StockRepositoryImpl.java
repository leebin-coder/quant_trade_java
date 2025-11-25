package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.domain.model.Stock;
import com.quant.market.domain.repository.StockRepository;
import com.quant.market.infrastructure.persistence.entity.StockEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stock Repository Implementation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

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
    public List<Stock> findByStockCodeIn(List<String> stockCodes) {
        return jpaRepository.findByStockCodeIn(stockCodes).stream()
                .map(StockEntity::toDomain)
                .collect(Collectors.toList());
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

    @Override
    public int batchUpsert(List<Stock> stocks) {
        if (stocks == null || stocks.isEmpty()) {
            return 0;
        }

        String sql = "INSERT INTO t_stock_basic " +
                "(exchange, stock_code, stock_name, listing_date, industry, status, area, " +
                "full_name, en_name, cn_spell, market, curr_type, delist_date, is_hs, " +
                "act_name, act_ent_type, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?::VARCHAR, ?, ?, ?, ?, ?, ?, ?, ?::VARCHAR, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) " +
                "ON CONFLICT (exchange, stock_code) " +
                "DO UPDATE SET " +
                "stock_name = EXCLUDED.stock_name, " +
                "listing_date = EXCLUDED.listing_date, " +
                "industry = EXCLUDED.industry, " +
                "status = EXCLUDED.status, " +
                "area = EXCLUDED.area, " +
                "full_name = EXCLUDED.full_name, " +
                "en_name = EXCLUDED.en_name, " +
                "cn_spell = EXCLUDED.cn_spell, " +
                "market = EXCLUDED.market, " +
                "curr_type = EXCLUDED.curr_type, " +
                "delist_date = EXCLUDED.delist_date, " +
                "is_hs = EXCLUDED.is_hs, " +
                "act_name = EXCLUDED.act_name, " +
                "act_ent_type = EXCLUDED.act_ent_type, " +
                "updated_at = CURRENT_TIMESTAMP";

        int[] updateCounts = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Stock stock = stocks.get(i);
                int paramIndex = 1;

                // INSERT values
                ps.setString(paramIndex++, stock.getExchange().name());
                ps.setString(paramIndex++, stock.getStockCode());
                ps.setString(paramIndex++, stock.getStockName());
                ps.setObject(paramIndex++, stock.getListingDate());
                ps.setString(paramIndex++, stock.getIndustry());
                ps.setString(paramIndex++, stock.getStatus() != null ? stock.getStatus().name() : null);
                ps.setString(paramIndex++, stock.getArea());
                ps.setString(paramIndex++, stock.getFullName());
                ps.setString(paramIndex++, stock.getEnName());
                ps.setString(paramIndex++, stock.getCnSpell());
                ps.setString(paramIndex++, stock.getMarket());
                ps.setString(paramIndex++, stock.getCurrType());
                ps.setObject(paramIndex++, stock.getDelistDate());
                ps.setString(paramIndex++, stock.getIsHs() != null ? stock.getIsHs().name() : null);
                ps.setString(paramIndex++, stock.getActName());
                ps.setString(paramIndex++, stock.getActEntType());
            }

            @Override
            public int getBatchSize() {
                return stocks.size();
            }
        });

        int totalAffected = 0;
        for (int count : updateCounts) {
            totalAffected += count;
        }

        log.info("Batch upsert completed: {} stocks processed, {} rows affected", stocks.size(), totalAffected);
        return totalAffected;
    }
}
