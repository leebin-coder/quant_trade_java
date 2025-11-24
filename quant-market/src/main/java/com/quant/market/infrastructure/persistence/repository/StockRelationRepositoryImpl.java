package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.domain.model.StockRelation;
import com.quant.market.domain.repository.StockRelationRepository;
import com.quant.market.infrastructure.persistence.entity.StockRelationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stock Relation Repository Implementation
 */
@Component
@RequiredArgsConstructor
public class StockRelationRepositoryImpl implements StockRelationRepository {

    private final StockRelationJpaRepository jpaRepository;

    @Override
    public StockRelation save(StockRelation stockRelation) {
        StockRelationEntity entity = StockRelationEntity.fromDomain(stockRelation);
        StockRelationEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<StockRelation> findById(Long id) {
        return jpaRepository.findById(id)
                .map(StockRelationEntity::toDomain);
    }

    @Override
    public Optional<StockRelation> findByStockCodeAndRefIdAndRefType(String stockCode, Long refId, StockRelation.RefType refType) {
        StockRelationEntity.RefType entityRefType = StockRelationEntity.RefType.valueOf(refType.name());
        return jpaRepository.findByStockCodeAndRefIdAndRefType(stockCode, refId, entityRefType)
                .map(StockRelationEntity::toDomain);
    }

    @Override
    public List<StockRelation> findByStockCode(String stockCode) {
        return jpaRepository.findByStockCode(stockCode).stream()
                .map(StockRelationEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockRelation> findByRefIdAndRefType(Long refId, StockRelation.RefType refType) {
        StockRelationEntity.RefType entityRefType = StockRelationEntity.RefType.valueOf(refType.name());
        return jpaRepository.findByRefIdAndRefType(refId, entityRefType).stream()
                .map(StockRelationEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockRelation> findByStockCodeAndRefType(String stockCode, StockRelation.RefType refType) {
        StockRelationEntity.RefType entityRefType = StockRelationEntity.RefType.valueOf(refType.name());
        return jpaRepository.findByStockCodeAndRefType(stockCode, entityRefType).stream()
                .map(StockRelationEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByStockCodeAndRefIdAndRefType(String stockCode, Long refId, StockRelation.RefType refType) {
        StockRelationEntity.RefType entityRefType = StockRelationEntity.RefType.valueOf(refType.name());
        return jpaRepository.existsByStockCodeAndRefIdAndRefType(stockCode, refId, entityRefType);
    }

    @Override
    public void deleteByStockCodeAndRefIdAndRefType(String stockCode, Long refId, StockRelation.RefType refType) {
        StockRelationEntity.RefType entityRefType = StockRelationEntity.RefType.valueOf(refType.name());
        jpaRepository.deleteByStockCodeAndRefIdAndRefType(stockCode, refId, entityRefType);
    }

    @Override
    public long countByStockCode(String stockCode) {
        return jpaRepository.countByStockCode(stockCode);
    }

    @Override
    public long countByRefIdAndRefType(Long refId, StockRelation.RefType refType) {
        StockRelationEntity.RefType entityRefType = StockRelationEntity.RefType.valueOf(refType.name());
        return jpaRepository.countByRefIdAndRefType(refId, entityRefType);
    }

    @Override
    public List<String> findFollowedStockCodes(List<String> stockCodes, Long userId, StockRelation.RefType refType) {
        if (stockCodes == null || stockCodes.isEmpty()) {
            return Collections.emptyList();
        }
        StockRelationEntity.RefType entityRefType = StockRelationEntity.RefType.valueOf(refType.name());
        List<StockRelationEntity> relations = jpaRepository.findByStockCodeInAndRefIdAndRefType(stockCodes, userId, entityRefType);
        return relations.stream()
                .map(StockRelationEntity::getStockCode)
                .collect(Collectors.toList());
    }
}
