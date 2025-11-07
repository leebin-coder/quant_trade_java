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
 * Stock Domain Model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class Stock extends BaseEntity {

    private Long id;
    private Exchange exchange;
    private String stockCode;
    private String stockName;
    private LocalDate listingDate;
    private String industry;
    private StockStatus status;
    private String area;
    private String fullName;
    private String enName;
    private String cnSpell;
    private String market;
    private String currType;
    private LocalDate delistDate;
    private IsHs isHs;
    private String actName;
    private String actEntType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Exchange Type
     */
    public enum Exchange {
        SSE("上交所"),
        SZSE("深交所"),
        BSE("北交所"),
        HKEX("港交所");

        private final String description;

        Exchange(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    /**
     * Stock Status
     */
    public enum StockStatus {
        L("上市"),
        D("退市"),
        P("暂停上市");

        private final String description;

        StockStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Is HuShen-Gang Tong
     */
    public enum IsHs {
        N("否"),
        H("沪股通"),
        S("深股通");

        private final String description;

        IsHs(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Get full stock code with exchange prefix
     */
    public String getFullCode() {
        return exchange.name() + "." + stockCode;
    }

    /**
     * Check if stock is tradable
     */
    public boolean isTradable() {
        return status == StockStatus.L;
    }
}
