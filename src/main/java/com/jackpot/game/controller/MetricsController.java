package com.jackpot.game.controller;

import com.jackpot.game.service.JackpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Metrics", description = "Custom metrics API")
public class MetricsController {
    
    private final JackpotService jackpotService;
    
    public MetricsController(JackpotService jackpotService) {
        this.jackpotService = jackpotService;
    }
    
    @GetMapping("/custom")
    @Operation(summary = "Get custom application metrics")
    public ResponseEntity<Map<String, Object>> getCustomMetrics() {
        Map<String, Object> metrics = Map.of(
            "total_jackpots", jackpotService.getTotalJackpots(),
            "total_bets_processed", jackpotService.getTotalBetsProcessed(),
            "total_amount_wagered", jackpotService.getTotalAmountWagered(),
            "total_wins", jackpotService.getTotalWins(),
            "total_amount_won", jackpotService.getTotalAmountWon(),
            "active_players", jackpotService.getActivePlayersCount()
        );
        return ResponseEntity.ok(metrics);
    }
}
