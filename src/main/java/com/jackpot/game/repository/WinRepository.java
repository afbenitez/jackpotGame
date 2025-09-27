package com.jackpot.game.repository;

import com.jackpot.game.entity.Win;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WinRepository extends JpaRepository<Win, Long> {
    
    @Query("SELECT w FROM Win w WHERE (:playerAlias IS NULL OR w.playerAlias LIKE %:playerAlias%) ORDER BY w.timestamp DESC")
    Page<Win> findWinsWithFilters(@Param("playerAlias") String playerAlias, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(w.winAmount), 0) FROM Win w")
    java.math.BigDecimal sumAllWinAmounts();
}
