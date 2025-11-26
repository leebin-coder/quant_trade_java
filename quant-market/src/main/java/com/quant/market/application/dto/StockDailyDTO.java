package com.quant.market.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quant.market.domain.model.StockDaily;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stock Daily DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDailyDTO {

    private Long id;

    private String stockCode;

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

    private Short adjustFlag;

    private BigDecimal turn;

    private Short tradeStatus;

    private BigDecimal peTtm;

    private BigDecimal pbMrq;

    private BigDecimal psTtm;

    private BigDecimal pcfNcfTtm;

    private Short isSt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Convert from domain model
     */
    public static StockDailyDTO fromDomain(StockDaily daily) {
        return StockDailyDTO.builder()
                .id(daily.getId())
                .stockCode(daily.getStockCode())
                .tradeDate(daily.getTradeDate())
                .openPrice(daily.getOpenPrice())
                .highPrice(daily.getHighPrice())
                .lowPrice(daily.getLowPrice())
                .closePrice(daily.getClosePrice())
                .preClose(daily.getPreClose())
                .changeAmount(daily.getChangeAmount())
                .pctChange(daily.getPctChange())
                .volume(daily.getVolume())
                .amount(daily.getAmount())
                .adjustFlag(daily.getAdjustFlag())
                .turn(daily.getTurn())
                .tradeStatus(daily.getTradeStatus())
                .peTtm(daily.getPeTtm())
                .pbMrq(daily.getPbMrq())
                .psTtm(daily.getPsTtm())
                .pcfNcfTtm(daily.getPcfNcfTtm())
                .isSt(daily.getIsSt())
                .createdAt(daily.getCreatedAt())
                .updatedAt(daily.getUpdatedAt())
                .build();
    }

    /**
     * Convert to domain model
     */
    public StockDaily toDomain() {
        return StockDaily.builder()
                .id(id)
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
                .adjustFlag(adjustFlag)
                .turn(turn)
                .tradeStatus(tradeStatus)
                .peTtm(peTtm)
                .pbMrq(pbMrq)
                .psTtm(psTtm)
                .pcfNcfTtm(pcfNcfTtm)
                .isSt(isSt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
