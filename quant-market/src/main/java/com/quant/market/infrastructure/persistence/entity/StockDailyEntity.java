package com.quant.market.infrastructure.persistence.entity;

import com.quant.market.domain.model.StockDaily;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stock Daily JPA Entity
 * 股票日线行情数据（未复权）
 * Reference: https://tushare.pro/document/2?doc_id=27
 */
@Data
@Entity
@Table(name = "t_stock_daily",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_stock_daily_unique",
           columnNames = {"stock_code", "trade_date"}
       ))
public class StockDailyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "open_price", precision = 10, scale = 2)
    private BigDecimal openPrice;

    @Column(name = "high_price", precision = 10, scale = 2)
    private BigDecimal highPrice;

    @Column(name = "low_price", precision = 10, scale = 2)
    private BigDecimal lowPrice;

    @Column(name = "close_price", precision = 10, scale = 2)
    private BigDecimal closePrice;

    @Column(name = "pre_close", precision = 10, scale = 2)
    private BigDecimal preClose;

    @Column(name = "change_amount", precision = 10, scale = 2)
    private BigDecimal changeAmount;

    @Column(name = "pct_change", precision = 10, scale = 4)
    private BigDecimal pctChange;

    @Column(name = "volume", precision = 20, scale = 2)
    private BigDecimal volume;

    @Column(name = "amount", precision = 20, scale = 2)
    private BigDecimal amount;

    @Column(name = "adjust_flag")
    private Short adjustFlag;

    @Column(name = "turn", precision = 10, scale = 6)
    private BigDecimal turn;

    @Column(name = "trade_status")
    private Short tradeStatus;

    @Column(name = "pe_ttm", precision = 10, scale = 4)
    private BigDecimal peTtm;

    @Column(name = "pb_mrq", precision = 10, scale = 4)
    private BigDecimal pbMrq;

    @Column(name = "ps_ttm", precision = 10, scale = 4)
    private BigDecimal psTtm;

    @Column(name = "pcf_ncf_ttm", precision = 10, scale = 4)
    private BigDecimal pcfNcfTtm;

    @Column(name = "is_st")
    private Short isSt;

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
    public StockDaily toDomain() {
        return StockDaily.builder()
                .id(id)
                .stockCode(stockCode)
                .tradeDate(tradeDate)
                .openPrice(openPrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .closePrice(closePrice)
                .preClose(preClose)
                .changeAmount(changeAmount)
                .pctChange(pctChange)
                .volume(volume)
                .amount(amount)
                .adjustFlag(adjustFlag)
                .turn(turn)
                .tradeStatus(tradeStatus)
                .peTtm(peTtm)
                .pbMrq(pbMrq)
                .psTtm(psTtm)
                .pcfNcfTtm(pcfNcfTtm)
                .isSt(isSt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    /**
     * Create from domain model
     */
    public static StockDailyEntity fromDomain(StockDaily daily) {
        StockDailyEntity entity = new StockDailyEntity();
        entity.setId(daily.getId());
        entity.setStockCode(daily.getStockCode());
        entity.setTradeDate(daily.getTradeDate());
        entity.setOpenPrice(daily.getOpenPrice());
        entity.setHighPrice(daily.getHighPrice());
        entity.setLowPrice(daily.getLowPrice());
        entity.setClosePrice(daily.getClosePrice());
        entity.setPreClose(daily.getPreClose());
        entity.setChangeAmount(daily.getChangeAmount());
        entity.setPctChange(daily.getPctChange());
        entity.setVolume(daily.getVolume());
        entity.setAmount(daily.getAmount());
        entity.setAdjustFlag(daily.getAdjustFlag());
        entity.setTurn(daily.getTurn());
        entity.setTradeStatus(daily.getTradeStatus());
        entity.setPeTtm(daily.getPeTtm());
        entity.setPbMrq(daily.getPbMrq());
        entity.setPsTtm(daily.getPsTtm());
        entity.setPcfNcfTtm(daily.getPcfNcfTtm());
        entity.setIsSt(daily.getIsSt());
        entity.setCreatedAt(daily.getCreatedAt());
        entity.setUpdatedAt(daily.getUpdatedAt());
        return entity;
    }
}
