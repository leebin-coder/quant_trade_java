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

    /**
     * 复权标识：1-后复权；2-前复权；3-不复权
     */
    private Short adjustFlag;

    /**
     * 换手率（%）
     */
    private BigDecimal turn;

    /**
     * 交易状态：1-正常交易；0-停牌
     */
    private Short tradeStatus;

    /**
     * 滚动市盈率
     */
    private BigDecimal peTtm;

    /**
     * 市净率
     */
    private BigDecimal pbMrq;

    /**
     * 滚动市销率
     */
    private BigDecimal psTtm;

    /**
     * 滚动市现率
     */
    private BigDecimal pcfNcfTtm;

    /**
     * 是否ST股：1-是；0-否
     */
    private Short isSt;

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
