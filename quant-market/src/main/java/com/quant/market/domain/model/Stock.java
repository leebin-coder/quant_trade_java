package com.quant.market.domain.model;

import com.quant.common.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String companyName;
    private LocalDate listingDate;
    private String industry;
    private StockStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Exchange Type
     */
    public enum Exchange {
        SH("上海证券交易所"),
        SZ("深圳证券交易所"),
        BJ("北京证券交易所"),
        HK("香港联合交易所"),
        US("美国证券交易所");

        private final String description;

        Exchange(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        /**
         * Auto detect exchange from stock code
         *
         * 交易所 代码前缀 股票类型 代码长度 说明
         * 上海	60	主板	6位	600000-605999
         * 上海	688, 689	科创板	6位	688001-689999
         * 上海	900	B股	6位	900901-900957
         * 深圳	000, 001, 002, 003	主板/中小板	6位	000001-004999
         * 深圳	300, 301, 302	创业板	6位	300001-302999
         * 深圳	200	B股	6位	200002-200999
         * 北京	43, 82, 83, 87	新三板/北交所	6位	430001-439999
         * 香港	任意数字	所有股票	5位	00001-09999
         * 美国	字母或1-4位数字	所有股票	可变	AAPL, TSLA等
         * @param stockCode Stock code
         * @return Detected exchange
         * @throws IllegalArgumentException if exchange cannot be determined
         */
        public static Exchange fromStockCode(String stockCode) {
            if (stockCode == null || stockCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Stock code cannot be null or empty");
            }

            String code = stockCode.trim().toUpperCase();

            // 处理带交易所前缀的代码
            if (code.contains(".")) {
                String[] parts = code.split("\\.");
                if (parts.length == 2) {
                    try {
                        return Exchange.valueOf(parts[0]);
                    } catch (IllegalArgumentException e) {
                        code = parts[1];
                    }
                }
            }

            // 检查是否为纯数字
            boolean isNumeric = code.matches("\\d+");

            if (isNumeric) {
                // 港股：5位数字
                if (code.length() == 5) {
                    return HK;
                }

                // A股：6位数字，且符合特定前缀规则
                if (code.length() == 6) {
                    // 上海证券交易所
                    if (code.startsWith("60") || code.startsWith("688") ||
                            code.startsWith("689") || code.startsWith("900")) {
                        return SH;
                    }

                    // 深圳证券交易所
                    if (code.startsWith("00") || code.startsWith("200") ||
                            code.startsWith("300") || code.startsWith("301") ||
                            code.startsWith("302")) {
                        return SZ;
                    }

                    // 北京证券交易所
                    if (code.startsWith("43") || code.startsWith("82") ||
                            code.startsWith("83") || code.startsWith("87")) {
                        return BJ;
                    }
                }
            }

            // 简化逻辑：不符合A股和港股规则的，全部识别为美股
            return US;
        }
    }

    /**
     * Stock Status
     */
    public enum StockStatus {
        LISTED("上市"),
        DELISTED("退市"),
        SUSPENDED("停牌");

        private final String description;

        StockStatus(String description) {
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
        return status == StockStatus.LISTED;
    }
}
