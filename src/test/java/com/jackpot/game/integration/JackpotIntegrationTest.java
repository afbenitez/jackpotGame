package com.jackpot.game.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JackpotIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void createJackpot_ShouldWorkEndToEnd() throws Exception {
        String requestJson = "{\"name\":\"Integration Test Jackpot\",\"winProbability\":0.1}";
        
        mockMvc.perform(post("/api/jackpots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jackpotId").exists())
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void getJackpots_ShouldWorkEndToEnd() throws Exception {
        mockMvc.perform(get("/api/jackpots"))
                .andExpect(status().isOk());
    }
    
    @Test
    void placeBet_ShouldWorkEndToEnd() throws Exception {
        String createJackpotJson = "{\"name\":\"Test Jackpot for Bet\",\"winProbability\":0.1}";
        mockMvc.perform(post("/api/jackpots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJackpotJson))
                .andExpect(status().isOk());
        
        String requestJson = "{\"playerAlias\":\"testplayer\",\"betAmount\":10}";
        
        mockMvc.perform(post("/api/jackpots/1/bet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winAmount").exists())
                .andExpect(jsonPath("$.newJackpotSize").exists())
                .andExpect(jsonPath("$.win").exists());
    }
    
    @Test
    void getWins_ShouldWorkEndToEnd() throws Exception {
        mockMvc.perform(get("/api/wins"))
                .andExpect(status().isOk());
    }
}
