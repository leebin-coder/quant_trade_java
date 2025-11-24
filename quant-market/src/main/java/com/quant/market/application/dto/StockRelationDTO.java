package com.quant.market.application.dto;

import com.quant.market.domain.model.StockRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Stock Relation Data Transfer Object
 * 股票关联关系传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRelationDTO {

    private Long id;
    private String stockCode;
    private Long refId;
    private String refType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert from domain model
     */
    public static StockRelationDTO fromDomain(StockRelation relation) {
        return StockRelationDTO.builder()
                .id(relation.getId())
                .stockCode(relation.getStockCode())
                .refId(relation.getRefId())
                .refType(relation.getRefType().name())
                .createdAt(relation.getCreatedAt())
                .updatedAt(relation.getUpdatedAt())
                .build();
    }
}
