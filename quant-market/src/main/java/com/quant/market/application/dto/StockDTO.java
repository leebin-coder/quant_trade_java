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
    private LocalDate listingDate;
    private String industry;
    private String status;
    private String area;
    private String fullName;
    private String enName;
    private String cnSpell;
    private String market;
    private String currType;
    private LocalDate delistDate;
    private String isHs;
    private String actName;
    private String actEntType;
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
                .listingDate(stock.getListingDate())
                .industry(stock.getIndustry())
                .status(stock.getStatus().name())
                .area(stock.getArea())
                .fullName(stock.getFullName())
                .enName(stock.getEnName())
                .cnSpell(stock.getCnSpell())
                .market(stock.getMarket())
                .currType(stock.getCurrType())
                .delistDate(stock.getDelistDate())
                .isHs(stock.getIsHs() != null ? stock.getIsHs().name() : null)
                .actName(stock.getActName())
                .actEntType(stock.getActEntType())
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
                .listingDate(listingDate)
                .industry(industry)
                .status(Stock.StockStatus.valueOf(status))
                .area(area)
                .fullName(fullName)
                .enName(enName)
                .cnSpell(cnSpell)
                .market(market)
                .currType(currType)
                .delistDate(delistDate)
                .isHs(isHs != null ? Stock.IsHs.valueOf(isHs) : null)
                .actName(actName)
                .actEntType(actEntType)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
