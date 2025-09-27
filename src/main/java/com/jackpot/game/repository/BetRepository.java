package com.jackpot.game.repository;

import com.jackpot.game.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByJackpotId(Long jackpotId);
    List<Bet> findByPlayerAlias(String playerAlias);
    
    @Query("SELECT COALESCE(SUM(b.betAmount), 0) FROM Bet b")
    BigDecimal sumAllBetAmounts();
    
    @Query("SELECT COUNT(DISTINCT b.playerAlias) FROM Bet b")
    long countDistinctPlayers();
}
