package com.jackpot.game.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BetRequest {
    
    @NotBlank(message = "Player alias is required")
    private String playerAlias;
    
    @NotNull(message = "Bet amount is required")
    @DecimalMin(value = "0.01", message = "Bet amount must be at least 0.01")
    private BigDecimal betAmount;
    
    public BetRequest() {}
    
    public BetRequest(String playerAlias, BigDecimal betAmount) {
        this.playerAlias = playerAlias;
        this.betAmount = betAmount;
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
}