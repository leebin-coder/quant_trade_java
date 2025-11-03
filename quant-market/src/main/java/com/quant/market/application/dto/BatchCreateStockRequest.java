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
     * Stock code (Required) - Exchange will be auto-detected
     */
    @NotBlank(message = "Stock code cannot be blank")
    private String stockCode;

    /**
     * Stock name (Required)
     */
    @NotBlank(message = "Stock name cannot be blank")
    private String stockName;

    /**
     * Company name (Required)
     */
    @NotBlank(message = "Company name cannot be blank")
    private String companyName;

    /**
     * Listing date (Required)
     */
    @NotNull(message = "Listing date cannot be null")
    private LocalDate listingDate;

    /**
     * Industry (Optional - can be null)
     */
    private String industry;

    /**
     * Latest price (Optional)
     */
    private BigDecimal latestPrice;

    /**
     * Previous close price (Optional)
     */
    private BigDecimal prevClosePrice;

    /**
     * Close price from 2 trading days ago (Optional)
     */
    private BigDecimal prevPrevClosePrice;

    /**
     * Total shares (Optional)
     */
    private BigDecimal totalShares;

    /**
     * Circulating shares (Optional)
     */
    private BigDecimal circulatingShares;

    /**
     * Total market cap (Optional)
     */
    private BigDecimal totalMarketCap;

    /**
     * Circulating market cap (Optional)
     */
    private BigDecimal circulatingMarketCap;

    /**
     * Convert to domain model
     * - Exchange: auto-detected from stock code
     * - Status: defaults to LISTED
     * - Industry: can be null
     */
    public Stock toDomain() {
        Stock.Exchange stockExchange = Stock.Exchange.fromStockCode(stockCode);

        return Stock.builder()
                .exchange(stockExchange)
                .stockCode(stockCode)
                .stockName(stockName)
                .companyName(companyName)
                .listingDate(listingDate)
                .industry(industry)
                .status(Stock.StockStatus.LISTED)  // Default to LISTED
                .latestPrice(latestPrice)
                .prevClosePrice(prevClosePrice)
                .prevPrevClosePrice(prevPrevClosePrice)
                .totalShares(totalShares)
                .circulatingShares(circulatingShares)
                .totalMarketCap(totalMarketCap)
                .circulatingMarketCap(circulatingMarketCap)
                .build();
    }
}
