package com.quant.market.application.dto;

import com.quant.market.domain.model.Stock;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
                .build();
    }
}
