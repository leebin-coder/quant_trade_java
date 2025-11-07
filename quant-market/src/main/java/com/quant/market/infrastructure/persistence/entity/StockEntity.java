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

    @Column(name = "listing_date", nullable = false)
    private LocalDate listingDate;

    @Column(length = 100)
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stock.StockStatus status;

    @Column(length = 100)
    private String area;

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(name = "en_name", length = 200)
    private String enName;

    @Column(name = "cn_spell", length = 50)
    private String cnSpell;

    @Column(length = 50)
    private String market;

    @Column(name = "curr_type", length = 10)
    private String currType;

    @Column(name = "delist_date")
    private LocalDate delistDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_hs", length = 1)
    private Stock.IsHs isHs;

    @Column(name = "act_name", length = 200)
    private String actName;

    @Column(name = "act_ent_type", length = 50)
    private String actEntType;

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
                .listingDate(listingDate)
                .industry(industry)
                .status(status)
                .area(area)
                .fullName(fullName)
                .enName(enName)
                .cnSpell(cnSpell)
                .market(market)
                .currType(currType)
                .delistDate(delistDate)
                .isHs(isHs)
                .actName(actName)
                .actEntType(actEntType)
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
        entity.setListingDate(stock.getListingDate());
        entity.setIndustry(stock.getIndustry());
        entity.setStatus(stock.getStatus());
        entity.setArea(stock.getArea());
        entity.setFullName(stock.getFullName());
        entity.setEnName(stock.getEnName());
        entity.setCnSpell(stock.getCnSpell());
        entity.setMarket(stock.getMarket());
        entity.setCurrType(stock.getCurrType());
        entity.setDelistDate(stock.getDelistDate());
        entity.setIsHs(stock.getIsHs());
        entity.setActName(stock.getActName());
        entity.setActEntType(stock.getActEntType());
        entity.setCreatedAt(stock.getCreatedAt());
        entity.setUpdatedAt(stock.getUpdatedAt());
        return entity;
    }
}
