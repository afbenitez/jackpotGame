package com.jackpot.game.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"player_alias", "jackpot_id", "timestamp"})
})
public class Bet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "jackpot_id", nullable = false)
    private Jackpot jackpot;
    
    @Column(name = "player_alias", nullable = false)
    private String playerAlias;
    
    @Column(name = "bet_amount", nullable = false)
    private BigDecimal betAmount;
    
    @Column(name = "timestamp", nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime timestamp;

    public Bet() {
    }

    public Bet(Jackpot jackpot, String playerAlias, BigDecimal betAmount) {
        this.jackpot = jackpot;
        this.playerAlias = playerAlias;
        this.betAmount = betAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jackpot getJackpot() {
        return jackpot;
    }

    public void setJackpot(Jackpot jackpot) {
        this.jackpot = jackpot;
    }

    public String getPlayerAlias() {
        return playerAlias;
    }

    public void setPlayerAlias(String playerAlias) {
        this.playerAlias = playerAlias;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
