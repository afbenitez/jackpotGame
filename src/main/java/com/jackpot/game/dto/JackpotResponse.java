package com.jackpot.game.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JackpotResponse {
    private Long jackpotId;
    private String name;
    private BigDecimal currentSize;
    private Integer numberOfWins;
    private LocalDateTime lastWinTimestamp;
    
    public JackpotResponse() {}
    
    public JackpotResponse(Long jackpotId, String name, BigDecimal currentSize, 
                          Integer numberOfWins, LocalDateTime lastWinTimestamp) {
        this.jackpotId = jackpotId;
        this.name = name;
        this.currentSize = currentSize;
        this.numberOfWins = numberOfWins;
        this.lastWinTimestamp = lastWinTimestamp;
    }
    
    public Long getJackpotId() {
        return jackpotId;
    }
    
    public void setJackpotId(Long jackpotId) {
        this.jackpotId = jackpotId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getCurrentSize() {
        return currentSize;
    }
    
    public void setCurrentSize(BigDecimal currentSize) {
        this.currentSize = currentSize;
    }
    
    public Integer getNumberOfWins() {
        return numberOfWins;
    }
    
    public void setNumberOfWins(Integer numberOfWins) {
        this.numberOfWins = numberOfWins;
    }
    
    public LocalDateTime getLastWinTimestamp() {
        return lastWinTimestamp;
    }
    
    public void setLastWinTimestamp(LocalDateTime lastWinTimestamp) {
        this.lastWinTimestamp = lastWinTimestamp;
    }
}
