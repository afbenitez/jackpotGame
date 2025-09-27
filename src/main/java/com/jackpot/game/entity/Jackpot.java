package com.jackpot.game.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jackpots")
public class Jackpot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "win_probability", nullable = false)
    private BigDecimal winProbability;
    
    @Column(name = "current_size", nullable = false)
    private BigDecimal currentSize = BigDecimal.ZERO;
    
    @Column(name = "win_count", nullable = false)
    private Integer winCount = 0;
    
    @Column(name = "last_win_timestamp")
    private LocalDateTime lastWinTimestamp;

    public Jackpot() {
    }

    public Jackpot(String name, BigDecimal winProbability) {
        this.name = name;
        this.winProbability = winProbability;
        this.currentSize = BigDecimal.ZERO;
        this.winCount = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getWinProbability() {
        return winProbability;
    }

    public void setWinProbability(BigDecimal winProbability) {
        this.winProbability = winProbability;
    }

    public BigDecimal getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(BigDecimal currentSize) {
        this.currentSize = currentSize;
    }

    public Integer getWinCount() {
        return winCount;
    }

    public void setWinCount(Integer winCount) {
        this.winCount = winCount;
    }

    public LocalDateTime getLastWinTimestamp() {
        return lastWinTimestamp;
    }

    public void setLastWinTimestamp(LocalDateTime lastWinTimestamp) {
        this.lastWinTimestamp = lastWinTimestamp;
    }
}
