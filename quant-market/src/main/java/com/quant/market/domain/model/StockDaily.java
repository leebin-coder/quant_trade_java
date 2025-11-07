package com.quant.market.domain.model;

import com.quant.common.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stock Daily Domain Model
 * 股票日线行情数据（未复权）
 * Reference: https://tushare.pro/document/2?doc_id=27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class StockDaily extends BaseEntity {

    private Long id;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 开盘价
     */
    private BigDecimal openPrice;

    /**
     * 最高价
     */
    private BigDecimal highPrice;

    /**
     * 最低价
     */
    private BigDecimal lowPrice;

    /**
     * 收盘价
     */
    private BigDecimal closePrice;

    /**
     * 昨收价（除权价，前复权）
     */
    private BigDecimal preClose;

    /**
     * 涨跌额
     */
    private BigDecimal changeAmount;

    /**
     * 涨跌幅（基于除权后的昨收计算）
     */
    private BigDecimal pctChange;

    /**
     * 成交量（手）
     */
    private BigDecimal volume;

    /**
     * 成交额（千元）
     */
    private BigDecimal amount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Check if price increased
     */
    public boolean isPriceUp() {
        return changeAmount != null && changeAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if price decreased
     */
    public boolean isPriceDown() {
        return changeAmount != null && changeAmount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Get price change direction
     */
    public String getPriceDirection() {
        if (changeAmount == null) {
            return "UNKNOWN";
        }
        int comparison = changeAmount.compareTo(BigDecimal.ZERO);
        if (comparison > 0) {
            return "UP";
        } else if (comparison < 0) {
            return "DOWN";
        } else {
            return "FLAT";
        }
    }
}
