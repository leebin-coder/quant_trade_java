package com.quant.market.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quant.market.infrastructure.persistence.entity.TradingCalendarEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Trading Calendar DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradingCalendarDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tradeDate;

    private Short isTradingDay;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Convert from entity
     */
    public static TradingCalendarDTO fromEntity(TradingCalendarEntity entity) {
        return TradingCalendarDTO.builder()
                .id(entity.getId())
                .tradeDate(entity.getTradeDate())
                .isTradingDay(entity.getIsTradingDay())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert to entity
     */
    public TradingCalendarEntity toEntity() {
        TradingCalendarEntity entity = new TradingCalendarEntity();
        entity.setId(id);
        entity.setTradeDate(tradeDate);
        entity.setIsTradingDay(isTradingDay);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);
        return entity;
    }
}
