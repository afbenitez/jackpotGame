package com.jackpot.game.dto;

import java.math.BigDecimal;

public class BetResponse {
    private BigDecimal winAmount;
    private BigDecimal newJackpotSize;
    private boolean isWin;
    
    public BetResponse() {}
    
    public BetResponse(BigDecimal winAmount, BigDecimal newJackpotSize, boolean isWin) {
        this.winAmount = winAmount;
        this.newJackpotSize = newJackpotSize;
        this.isWin = isWin;
    }
    
    public BigDecimal getWinAmount() {
        return winAmount;
    }
    
    public void setWinAmount(BigDecimal winAmount) {
        this.winAmount = winAmount;
    }
    
    public BigDecimal getNewJackpotSize() {
        return newJackpotSize;
    }
    
    public void setNewJackpotSize(BigDecimal newJackpotSize) {
        this.newJackpotSize = newJackpotSize;
    }
    
    public boolean isWin() {
        return isWin;
    }
    
    public void setWin(boolean win) {
        isWin = win;
    }
}