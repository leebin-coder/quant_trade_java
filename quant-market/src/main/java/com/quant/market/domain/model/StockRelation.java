package com.quant.market.domain.model;

import com.quant.common.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Stock Relation Domain Model
 * 股票关联领域模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class StockRelation extends BaseEntity {

    private Long id;
    private String stockCode;
    private Long refId;
    private RefType refType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 关联类型枚举
     */
    public enum RefType {
        /**
         * 用户关注的股票
         */
        STOCKS_USER_FOLLOWED("用户关注");

        private final String description;

        RefType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
