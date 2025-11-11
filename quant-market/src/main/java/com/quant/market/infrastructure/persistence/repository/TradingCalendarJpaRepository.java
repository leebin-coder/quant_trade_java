package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.infrastructure.persistence.entity.TradingCalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Trading Calendar JPA Repository
 */
@Repository
public interface TradingCalendarJpaRepository extends JpaRepository<TradingCalendarEntity, Long> {

    /**
     * Find latest trading day
     */
    @Query("SELECT t FROM TradingCalendarEntity t ORDER BY t.tradeDate DESC LIMIT 1")
    Optional<TradingCalendarEntity> findLatestTradingDay();

    /**
     * Find by year
     */
    @Query("SELECT t FROM TradingCalendarEntity t WHERE YEAR(t.tradeDate) = :year ORDER BY t.tradeDate ASC")
    List<TradingCalendarEntity> findByYear(@Param("year") int year);

    /**
     * Find by trade date
     */
    Optional<TradingCalendarEntity> findByTradeDate(LocalDate tradeDate);
}
