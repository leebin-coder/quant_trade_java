package com.quant.market.application.dto;

import com.quant.market.domain.model.Stock;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
                .build();
    }
}
