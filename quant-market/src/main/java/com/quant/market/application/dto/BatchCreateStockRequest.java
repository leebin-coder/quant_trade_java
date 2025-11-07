package com.quant.market.application.dto;

import com.quant.market.domain.model.Stock;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Batch Create Stock Item DTO
 * Simplified version with minimal required fields
 */
@Data
public class BatchCreateStockRequest {

    /**
     * Exchange (Required)
     */
    @NotBlank(message = "Exchange cannot be blank")
    private String exchange;

    /**
     * Stock code (Required)
     */
    @NotBlank(message = "Stock code cannot be blank")
    private String stockCode;

    /**
     * Stock name (Optional)
     */
    private String stockName;

    /**
     * Listing date (Optional)
     */
    private LocalDate listingDate;

    /**
     * Industry (Optional - can be null)
     */
    private String industry;

    /**
     * Stock status (Optional - defaults to L)
     */
    private String status;

    /**
     * Area (Optional)
     */
    private String area;

    /**
     * Full name (Optional)
     */
    private String fullName;

    /**
     * English name (Optional)
     */
    private String enName;

    /**
     * Chinese pinyin abbreviation (Optional)
     */
    private String cnSpell;

    /**
     * Market type (Optional)
     */
    private String market;

    /**
     * Trading currency (Optional)
     */
    private String currType;

    /**
     * Delisting date (Optional)
     */
    private LocalDate delistDate;

    /**
     * HuShen-Gang Tong status (Optional)
     */
    private String isHs;

    /**
     * Actual controller name (Optional)
     */
    private String actName;

    /**
     * Actual controller entity type (Optional)
     */
    private String actEntType;

    /**
     * Convert to domain model
     * - Status: defaults to L
     * - Industry: can be null
     */
    public Stock toDomain() {
        return Stock.builder()
                .exchange(Stock.Exchange.valueOf(exchange.toUpperCase()))
                .stockCode(stockCode)
                .stockName(stockName)
                .listingDate(listingDate)
                .industry(industry)
                .status(status != null ? Stock.StockStatus.valueOf(status.toUpperCase()) : Stock.StockStatus.L)
                .area(area)
                .fullName(fullName)
                .enName(enName)
                .cnSpell(cnSpell)
                .market(market)
                .currType(currType)
                .delistDate(delistDate)
                .isHs(isHs != null ? Stock.IsHs.valueOf(isHs.toUpperCase()) : null)
                .actName(actName)
                .actEntType(actEntType)
                .build();
    }
}
