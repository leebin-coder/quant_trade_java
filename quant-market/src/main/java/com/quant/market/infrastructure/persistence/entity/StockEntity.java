package com.quant.market.infrastructure.persistence.entity;

import com.quant.market.domain.model.Stock;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stock JPA Entity
 */
@Data
@Entity
@Table(name = "t_stock_basic")
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stock.Exchange exchange;

    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "listing_date", nullable = false)
    private LocalDate listingDate;

    @Column(length = 100)
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stock.StockStatus status;

    @Column(name = "latest_price", precision = 18, scale = 4)
    private BigDecimal latestPrice;

    @Column(name = "prev_close_price", precision = 18, scale = 4)
    private BigDecimal prevClosePrice;

    @Column(name = "prev_prev_close_price", precision = 18, scale = 4)
    private BigDecimal prevPrevClosePrice;

    @Column(name = "total_shares", precision = 20, scale = 2)
    private BigDecimal totalShares;

    @Column(name = "circulating_shares", precision = 20, scale = 2)
    private BigDecimal circulatingShares;

    @Column(name = "total_market_cap", precision = 18, scale = 2)
    private BigDecimal totalMarketCap;

    @Column(name = "circulating_market_cap", precision = 18, scale = 2)
    private BigDecimal circulatingMarketCap;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Convert to domain model
     */
    public Stock toDomain() {
        return Stock.builder()
                .id(id)
                .exchange(exchange)
                .stockCode(stockCode)
                .stockName(stockName)
                .companyName(companyName)
                .listingDate(listingDate)
                .industry(industry)
                .status(status)
                .latestPrice(latestPrice)
                .prevClosePrice(prevClosePrice)
                .prevPrevClosePrice(prevPrevClosePrice)
                .totalShares(totalShares)
                .circulatingShares(circulatingShares)
                .totalMarketCap(totalMarketCap)
                .circulatingMarketCap(circulatingMarketCap)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    /**
     * Create from domain model
     */
    public static StockEntity fromDomain(Stock stock) {
        StockEntity entity = new StockEntity();
        entity.setId(stock.getId());
        entity.setExchange(stock.getExchange());
        entity.setStockCode(stock.getStockCode());
        entity.setStockName(stock.getStockName());
        entity.setCompanyName(stock.getCompanyName());
        entity.setListingDate(stock.getListingDate());
        entity.setIndustry(stock.getIndustry());
        entity.setStatus(stock.getStatus());
        entity.setLatestPrice(stock.getLatestPrice());
        entity.setPrevClosePrice(stock.getPrevClosePrice());
        entity.setPrevPrevClosePrice(stock.getPrevPrevClosePrice());
        entity.setTotalShares(stock.getTotalShares());
        entity.setCirculatingShares(stock.getCirculatingShares());
        entity.setTotalMarketCap(stock.getTotalMarketCap());
        entity.setCirculatingMarketCap(stock.getCirculatingMarketCap());
        entity.setCreatedAt(stock.getCreatedAt());
        entity.setUpdatedAt(stock.getUpdatedAt());
        return entity;
    }
}
