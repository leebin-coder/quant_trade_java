package com.quant.market.infrastructure.persistence.entity;

import com.quant.market.domain.model.StockRelation;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Stock Relation JPA Entity
 * 股票关联表，用于记录股票与其他实体的关联关系
 */
@Data
@Entity
@Table(name = "t_stock_relation", indexes = {
    @Index(name = "idx_stock_relation_stock_code", columnList = "stock_code"),
    @Index(name = "idx_stock_relation_ref", columnList = "ref_id,ref_type"),
    @Index(name = "idx_stock_relation_created_at", columnList = "created_at")
})
public class StockRelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ref_type", nullable = false, length = 50)
    private RefType refType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 关联类型枚举
     */
    public enum RefType {
        /**
         * 用户关注的股票
         */
        STOCKS_USER_FOLLOWED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Convert to domain model
     */
    public StockRelation toDomain() {
        return StockRelation.builder()
                .id(id)
                .stockCode(stockCode)
                .refId(refId)
                .refType(StockRelation.RefType.valueOf(refType.name()))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    /**
     * Create from domain model
     */
    public static StockRelationEntity fromDomain(StockRelation stockRelation) {
        StockRelationEntity entity = new StockRelationEntity();
        entity.setId(stockRelation.getId());
        entity.setStockCode(stockRelation.getStockCode());
        entity.setRefId(stockRelation.getRefId());
        entity.setRefType(RefType.valueOf(stockRelation.getRefType().name()));
        entity.setCreatedAt(stockRelation.getCreatedAt());
        entity.setUpdatedAt(stockRelation.getUpdatedAt());
        return entity;
    }
}
