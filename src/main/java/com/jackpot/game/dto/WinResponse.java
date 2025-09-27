package com.jackpot.game.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WinResponse {
    private Long winId;
    private String playerAlias;
    private BigDecimal winAmount;
    private LocalDateTime timestamp;
    private String jackpotName;
    
    public WinResponse() {}
    
    public WinResponse(Long winId, String playerAlias, BigDecimal winAmount, 
                      LocalDateTime timestamp, String jackpotName) {
        this.winId = winId;
        this.playerAlias = playerAlias;
        this.winAmount = winAmount;
        this.timestamp = timestamp;
        this.jackpotName = jackpotName;
    }
    
    public Long getWinId() {
        return winId;
    }
    
    public void setWinId(Long winId) {
        this.winId = winId;
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
    
    public String getJackpotName() {
        return jackpotName;
    }
    
    public void setJackpotName(String jackpotName) {
        this.jackpotName = jackpotName;
    }
}
