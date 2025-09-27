package com.jackpot.game.service;

import com.jackpot.game.dto.BetResponse;
import com.jackpot.game.dto.JackpotResponse;
import com.jackpot.game.dto.WinResponse;
import com.jackpot.game.entity.Bet;
import com.jackpot.game.entity.Jackpot;
import com.jackpot.game.entity.Win;
import com.jackpot.game.exception.JackpotAlreadyExistsException;
import com.jackpot.game.repository.BetRepository;
import com.jackpot.game.repository.JackpotRepository;
import com.jackpot.game.repository.WinRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JackpotService {
    
    private final JackpotRepository jackpotRepository;
    private final BetRepository betRepository;
    private final WinRepository winRepository;
    private final SecureRandom random = new SecureRandom();
    
    public JackpotService(JackpotRepository jackpotRepository, BetRepository betRepository, WinRepository winRepository) {
        this.jackpotRepository = jackpotRepository;
        this.betRepository = betRepository;
        this.winRepository = winRepository;
    }
    
    public Jackpot createJackpot(String name, BigDecimal winProbability) {
        if (jackpotRepository.existsByName(name)) {
            throw new JackpotAlreadyExistsException("A jackpot with name '" + name + "' already exists");
        }
        
        if (winProbability.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Win probability cannot be greater than 1.0");
        }
        
        Jackpot jackpot = new Jackpot(name, winProbability);
        return jackpotRepository.save(jackpot);
    }
    
    @Transactional(readOnly = true)
    public List<JackpotResponse> getAllJackpots() {
        return jackpotRepository.findAll().stream()
            .map(jackpot -> new JackpotResponse(
                jackpot.getId(),
                jackpot.getName(),
                jackpot.getCurrentSize(),
                jackpot.getWinCount(),
                jackpot.getLastWinTimestamp()
            ))
            .collect(Collectors.toList());
    }
    
    public BetResponse placeBet(Long jackpotId, String playerAlias, BigDecimal betAmount) {
        Jackpot jackpot = jackpotRepository.findById(jackpotId)
            .orElseThrow(() -> new RuntimeException("Jackpot not found"));
        
        Bet bet = new Bet(jackpot, playerAlias, betAmount);
        betRepository.save(bet);
        
        jackpot.setCurrentSize(jackpot.getCurrentSize().add(betAmount));
        
        boolean isWin = random.nextDouble() < jackpot.getWinProbability().doubleValue();
        BigDecimal winAmount = BigDecimal.ZERO;
        
        if (isWin) {
            winAmount = jackpot.getCurrentSize();
            
            Win win = new Win(jackpot, playerAlias, winAmount);
            winRepository.save(win);
            
            jackpot.setWinCount(jackpot.getWinCount() + 1);
            jackpot.setLastWinTimestamp(LocalDateTime.now());
            jackpot.setCurrentSize(BigDecimal.ZERO);
        }
        
        jackpotRepository.save(jackpot);
        
        return new BetResponse(winAmount, jackpot.getCurrentSize(), isWin);
    }
    
    @Transactional(readOnly = true)
    public List<WinResponse> getWins(String playerAlias, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<Win> wins = winRepository.findWinsWithFilters(playerAlias, pageable);
        
        return wins.getContent().stream()
            .map(win -> new WinResponse(
                win.getId(),
                win.getPlayerAlias(),
                win.getWinAmount(),
                win.getTimestamp(),
                win.getJackpot().getName()
            ))
            .collect(Collectors.toList());
    }
    
    //Metrics methods
    @Transactional(readOnly = true)
    public long getTotalJackpots() {
        return jackpotRepository.count();
    }
    
    @Transactional(readOnly = true)
    public long getTotalBetsProcessed() {
        return betRepository.count();
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountWagered() {
        return betRepository.sumAllBetAmounts();
    }
    
    @Transactional(readOnly = true)
    public long getTotalWins() {
        return winRepository.count();
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountWon() {
        return winRepository.sumAllWinAmounts();
    }
    
    @Transactional(readOnly = true)
    public long getActivePlayersCount() {
        return betRepository.countDistinctPlayers();
    }
}
