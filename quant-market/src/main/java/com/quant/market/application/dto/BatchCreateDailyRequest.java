package com.quant.market.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quant.market.domain.model.StockDaily;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Batch Create Daily Request
 * Used for batch insert operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchCreateDailyRequest {

    @NotBlank(message = "Stock code is required")
    private String stockCode;

    @NotNull(message = "Trade date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tradeDate;

    private BigDecimal openPrice;

    private BigDecimal highPrice;

    private BigDecimal lowPrice;

    private BigDecimal closePrice;

    private BigDecimal preClose;

    private BigDecimal changeAmount;

    private BigDecimal pctChange;

    private BigDecimal volume;

    private BigDecimal amount;

    /**
     * Convert to domain model
     */
    public StockDaily toDomain() {
        return StockDaily.builder()
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
                .build();
    }
}
