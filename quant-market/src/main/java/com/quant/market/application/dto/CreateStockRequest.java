package com.quant.market.application.dto;

import com.quant.market.domain.model.Stock;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Create Stock Request DTO
 */
@Data
public class CreateStockRequest {

    /**
     * Exchange (Optional - will be auto-detected from stock code if not provided)
     */
    private String exchange;

    /**
     * Stock code (Required)
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
     * Industry (Optional)
     */
    private String industry;

    /**
     * Stock status (Optional - defaults to LISTED)
     */
    private String status;

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
     * If exchange is not provided, it will be auto-detected from stock code
     */
    public Stock toDomain() {
        // Auto-detect exchange from stock code if not provided
        Stock.Exchange stockExchange;
        if (exchange == null || exchange.trim().isEmpty()) {
            stockExchange = Stock.Exchange.fromStockCode(stockCode);
        } else {
            stockExchange = Stock.Exchange.valueOf(exchange.toUpperCase());
        }

        return Stock.builder()
                .exchange(stockExchange)
                .stockCode(stockCode)
                .stockName(stockName)
                .companyName(companyName)
                .listingDate(listingDate)
                .industry(industry)
                .status(status != null ? Stock.StockStatus.valueOf(status.toUpperCase()) : Stock.StockStatus.LISTED)
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
