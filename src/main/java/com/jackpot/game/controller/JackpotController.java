package com.jackpot.game.controller;

import com.jackpot.game.dto.*;
import com.jackpot.game.entity.Jackpot;
import com.jackpot.game.service.JackpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Jackpot", description = "Jackpot game API")
public class JackpotController {
    
    private final JackpotService jackpotService;
    
    public JackpotController(JackpotService jackpotService) {
        this.jackpotService = jackpotService;
    }
    
    @PostMapping("/jackpots")
    @Operation(summary = "Create a new jackpot")
    public ResponseEntity<CreateJackpotResponse> createJackpot(
            @Valid @RequestBody CreateJackpotRequest request) {
        
        Jackpot jackpot = jackpotService.createJackpot(request.getName(), request.getWinProbability());
        CreateJackpotResponse response = new CreateJackpotResponse(
            jackpot.getId(),
            "Jackpot created successfully"
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/jackpots")
    @Operation(summary = "Get all jackpots")
    public ResponseEntity<List<JackpotResponse>> getAllJackpots() {
        List<JackpotResponse> jackpots = jackpotService.getAllJackpots();
        return ResponseEntity.ok(jackpots);
    }
    
    @PostMapping("/jackpots/{id}/bet")
    @Operation(summary = "Place a bet on a jackpot")
    public ResponseEntity<BetResponse> placeBet(
            @PathVariable Long id,
            @Valid @RequestBody BetRequest request) {
        
        BetResponse response = jackpotService.placeBet(id, request.getPlayerAlias(), request.getBetAmount());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/wins")
    @Operation(summary = "Get wins with optional filtering")
    public ResponseEntity<List<WinResponse>> getWins(
            @RequestParam(required = false) String playerAlias,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        
        List<WinResponse> wins = jackpotService.getWins(playerAlias, page, size);
        return ResponseEntity.ok(wins);
    }
}