package com.quant.market.domain.repository;

import com.quant.market.domain.model.StockRelation;

import java.util.List;
import java.util.Optional;

/**
 * Stock Relation Repository Interface
 * 股票关联仓储接口
 */
public interface StockRelationRepository {

    /**
     * 保存关联关系
     */
    StockRelation save(StockRelation stockRelation);

    /**
     * 根据ID查找
     */
    Optional<StockRelation> findById(Long id);

    /**
     * 查找特定关联关系
     */
    Optional<StockRelation> findByStockCodeAndRefIdAndRefType(String stockCode, Long refId, StockRelation.RefType refType);

    /**
     * 根据股票代码查询所有关联
     */
    List<StockRelation> findByStockCode(String stockCode);

    /**
     * 根据关联ID和类型查询所有关联的股票
     */
    List<StockRelation> findByRefIdAndRefType(Long refId, StockRelation.RefType refType);

    /**
     * 根据股票代码和关联类型查询
     */
    List<StockRelation> findByStockCodeAndRefType(String stockCode, StockRelation.RefType refType);

    /**
     * 检查特定关联关系是否存在
     */
    boolean existsByStockCodeAndRefIdAndRefType(String stockCode, Long refId, StockRelation.RefType refType);

    /**
     * 删除特定关联关系
     */
    void deleteByStockCodeAndRefIdAndRefType(String stockCode, Long refId, StockRelation.RefType refType);

    /**
     * 统计股票的关联数量
     */
    long countByStockCode(String stockCode);

    /**
     * 统计某个实体的被关联数量
     */
    long countByRefIdAndRefType(Long refId, StockRelation.RefType refType);

    /**
     * 批量查询用户关注的股票代码集合
     * @param stockCodes 股票代码列表
     * @param userId 用户ID
     * @param refType 关联类型
     * @return 用户关注的股票代码集合
     */
    List<String> findFollowedStockCodes(List<String> stockCodes, Long userId, StockRelation.RefType refType);
}
