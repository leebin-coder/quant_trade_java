package com.quant.market.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Update Stock Request DTO
 */
@Data
public class UpdateStockRequest {

    private String stockName;
    private String companyName;
    private LocalDate listingDate;
    private String industry;
    private String status;
    private BigDecimal latestPrice;
    private BigDecimal prevClosePrice;
    private BigDecimal prevPrevClosePrice;
    private BigDecimal totalShares;
    private BigDecimal circulatingShares;
    private BigDecimal totalMarketCap;
    private BigDecimal circulatingMarketCap;
}
