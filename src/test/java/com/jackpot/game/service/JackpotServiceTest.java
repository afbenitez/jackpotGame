package com.jackpot.game.service;

import com.jackpot.game.dto.BetResponse;
import com.jackpot.game.dto.JackpotResponse;
import com.jackpot.game.entity.Jackpot;
import com.jackpot.game.repository.BetRepository;
import com.jackpot.game.repository.JackpotRepository;
import com.jackpot.game.repository.WinRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JackpotServiceTest {
    
    @Mock
    private JackpotRepository jackpotRepository;
    
    @Mock
    private BetRepository betRepository;
    
    @Mock
    private WinRepository winRepository;
    
    @InjectMocks
    private JackpotService jackpotService;
    
    @Test
    void createJackpot_ShouldReturnJackpot() {
        Jackpot jackpot = new Jackpot("Test Jackpot", BigDecimal.valueOf(0.1));
        jackpot.setId(1L);
        when(jackpotRepository.existsByName(anyString())).thenReturn(false);
        when(jackpotRepository.save(any(Jackpot.class))).thenReturn(jackpot);

        Jackpot result = jackpotService.createJackpot("Test Jackpot", BigDecimal.valueOf(0.1));

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Jackpot");
    }
    
    @Test
    void getAllJackpots_ShouldReturnJackpotResponses() {
        List<Jackpot> jackpots = List.of(
            new Jackpot("Jackpot 1", BigDecimal.valueOf(0.1)),
            new Jackpot("Jackpot 2", BigDecimal.valueOf(0.2))
        );
        when(jackpotRepository.findAll()).thenReturn(jackpots);

        List<JackpotResponse> result = jackpotService.getAllJackpots();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Jackpot 1");
    }
    
    @Test
    void placeBet_ShouldReturnBetResponse() {
        Jackpot jackpot = new Jackpot("Test Jackpot", BigDecimal.valueOf(0.1));
        jackpot.setId(1L);
        jackpot.setCurrentSize(BigDecimal.valueOf(100));
        
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(jackpotRepository.save(any(Jackpot.class))).thenReturn(jackpot);

        BetResponse result = jackpotService.placeBet(1L, "player1", BigDecimal.valueOf(10));

        assertThat(result).isNotNull();
        assertThat(result.getNewJackpotSize()).isNotNull();
    }
    
    @Test
    void createJackpot_WithDuplicateName_ShouldThrowJackpotAlreadyExistsException() {
        when(jackpotRepository.existsByName(anyString())).thenReturn(true);

        assertThatThrownBy(() -> jackpotService.createJackpot("Existing Jackpot", BigDecimal.valueOf(0.1)))
                .isInstanceOf(com.jackpot.game.exception.JackpotAlreadyExistsException.class)
                .hasMessageContaining("A jackpot with name 'Existing Jackpot' already exists");
    }
    
    @Test
    void createJackpot_WithProbabilityGreaterThanOne_ShouldThrowIllegalArgumentException() {
        when(jackpotRepository.existsByName(anyString())).thenReturn(false);

        assertThatThrownBy(() -> jackpotService.createJackpot("Test Jackpot", new BigDecimal("1.5")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Win probability cannot be greater than 1.0");
    }
    
    @Test
    void createJackpot_WithValidProbabilityEqualToOne_ShouldReturnJackpot() {
        Jackpot expectedJackpot = new Jackpot("Test Jackpot", BigDecimal.ONE);
        expectedJackpot.setId(1L);
        
        when(jackpotRepository.existsByName(anyString())).thenReturn(false);
        when(jackpotRepository.save(any(Jackpot.class))).thenReturn(expectedJackpot);
        Jackpot result = jackpotService.createJackpot("Test Jackpot", BigDecimal.ONE);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Jackpot");
        assertThat(result.getWinProbability()).isEqualByComparingTo(BigDecimal.ONE);
    }
}