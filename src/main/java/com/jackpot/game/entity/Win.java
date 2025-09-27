package com.jackpot.game.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wins")
public class Win {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "jackpot_id", nullable = false)
    private Jackpot jackpot;
    
    @Column(name = "player_alias", nullable = false)
    private String playerAlias;
    
    @Column(name = "win_amount", nullable = false)
    private BigDecimal winAmount;
    
    @Column(name = "timestamp", nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime timestamp;

    public Win() {
    }

    public Win(Jackpot jackpot, String playerAlias, BigDecimal winAmount) {
        this.jackpot = jackpot;
        this.playerAlias = playerAlias;
        this.winAmount = winAmount;
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

    public BigDecimal getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(BigDecimal winAmount) {
        this.winAmount = winAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
