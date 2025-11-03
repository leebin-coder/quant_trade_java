package com.quant.market.application.dto;

import com.quant.market.domain.model.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stock Data Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  StockDTO {

    private Long id;
    private String exchange;
    private String stockCode;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert from domain model
     */
    public static StockDTO fromDomain(Stock stock) {
        return StockDTO.builder()
                .id(stock.getId())
                .exchange(stock.getExchange().name())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .companyName(stock.getCompanyName())
                .listingDate(stock.getListingDate())
                .industry(stock.getIndustry())
                .status(stock.getStatus().name())
                .latestPrice(stock.getLatestPrice())
                .prevClosePrice(stock.getPrevClosePrice())
                .prevPrevClosePrice(stock.getPrevPrevClosePrice())
                .totalShares(stock.getTotalShares())
                .circulatingShares(stock.getCirculatingShares())
                .totalMarketCap(stock.getTotalMarketCap())
                .circulatingMarketCap(stock.getCirculatingMarketCap())
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }

    /**
     * Convert to domain model
     */
    public Stock toDomain() {
        return Stock.builder()
                .id(id)
                .exchange(Stock.Exchange.valueOf(exchange))
                .stockCode(stockCode)
                .stockName(stockName)
                .companyName(companyName)
                .listingDate(listingDate)
                .industry(industry)
                .status(Stock.StockStatus.valueOf(status))
                .latestPrice(latestPrice)
                .prevClosePrice(prevClosePrice)
                .prevPrevClosePrice(prevPrevClosePrice)
                .totalShares(totalShares)
                .circulatingShares(circulatingShares)
                .totalMarketCap(totalMarketCap)
                .circulatingMarketCap(circulatingMarketCap)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
