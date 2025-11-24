package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.infrastructure.persistence.entity.StockRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Stock Relation JPA Repository
 */
@Repository
public interface StockRelationJpaRepository extends JpaRepository<StockRelationEntity, Long>, JpaSpecificationExecutor<StockRelationEntity> {

    /**
     * 根据股票代码查询所有关联关系
     */
    List<StockRelationEntity> findByStockCode(String stockCode);

    /**
     * 根据关联ID和类型查询所有关联关系
     */
    List<StockRelationEntity> findByRefIdAndRefType(Long refId, StockRelationEntity.RefType refType);

    /**
     * 根据股票代码和关联类型查询
     */
    List<StockRelationEntity> findByStockCodeAndRefType(String stockCode, StockRelationEntity.RefType refType);

    /**
     * 查询特定关联关系是否存在
     */
    boolean existsByStockCodeAndRefIdAndRefType(String stockCode, Long refId, StockRelationEntity.RefType refType);

    /**
     * 查询特定关联关系
     */
    Optional<StockRelationEntity> findByStockCodeAndRefIdAndRefType(String stockCode, Long refId, StockRelationEntity.RefType refType);

    /**
     * 删除特定关联关系
     */
    void deleteByStockCodeAndRefIdAndRefType(String stockCode, Long refId, StockRelationEntity.RefType refType);

    /**
     * 统计股票的关联数量
     */
    long countByStockCode(String stockCode);

    /**
     * 统计某个实体的被关联数量
     */
    long countByRefIdAndRefType(Long refId, StockRelationEntity.RefType refType);

    /**
     * 批量查询用户关注的股票（根据股票代码列表和用户ID）
     */
    List<StockRelationEntity> findByStockCodeInAndRefIdAndRefType(List<String> stockCodes, Long refId, StockRelationEntity.RefType refType);
}
