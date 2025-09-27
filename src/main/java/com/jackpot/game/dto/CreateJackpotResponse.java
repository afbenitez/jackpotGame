package com.jackpot.game.dto;

public class CreateJackpotResponse {
    private Long jackpotId;
    private String message;
    
    public CreateJackpotResponse() {}
    
    public CreateJackpotResponse(Long jackpotId, String message) {
        this.jackpotId = jackpotId;
        this.message = message;
    }
    
    public Long getJackpotId() {
        return jackpotId;
    }
    
    public void setJackpotId(Long jackpotId) {
        this.jackpotId = jackpotId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
