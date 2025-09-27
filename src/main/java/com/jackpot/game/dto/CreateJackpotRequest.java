package com.jackpot.game.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateJackpotRequest {
    
    @NotBlank(message = "Jackpot name is required")
    private String name;
    
    @NotNull(message = "Win probability is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Win probability must be greater than 0")
    @jakarta.validation.constraints.DecimalMax(value = "1.0", inclusive = true, message = "Win probability cannot be greater than 1.0")
    private BigDecimal winProbability;
    
    public CreateJackpotRequest() {}
    
    public CreateJackpotRequest(String name, BigDecimal winProbability) {
        this.name = name;
        this.winProbability = winProbability;
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
}