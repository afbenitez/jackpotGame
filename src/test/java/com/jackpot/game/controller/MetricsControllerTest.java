package com.jackpot.game.controller;

import com.jackpot.game.service.JackpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MetricsControllerTest {

    @Mock
    private JackpotService jackpotService;

    private MockMvc mockMvc;
    private MetricsController metricsController;

    @BeforeEach
    void setUp() {
        metricsController = new MetricsController(jackpotService);
        mockMvc = MockMvcBuilders.standaloneSetup(metricsController).build();
    }

    @Test
    void getCustomMetrics_ShouldReturnAllMetrics() throws Exception {
        when(jackpotService.getTotalJackpots()).thenReturn(5L);
        when(jackpotService.getTotalBetsProcessed()).thenReturn(150L);
        when(jackpotService.getTotalAmountWagered()).thenReturn(new BigDecimal("1500.00"));
        when(jackpotService.getTotalWins()).thenReturn(25L);
        when(jackpotService.getTotalAmountWon()).thenReturn(new BigDecimal("750.00"));
        when(jackpotService.getActivePlayersCount()).thenReturn(12L);

        mockMvc.perform(get("/api/metrics/custom"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_jackpots").value(5))
                .andExpect(jsonPath("$.total_bets_processed").value(150))
                .andExpect(jsonPath("$.total_amount_wagered").value(1500.00))
                .andExpect(jsonPath("$.total_wins").value(25))
                .andExpect(jsonPath("$.total_amount_won").value(750.00))
                .andExpect(jsonPath("$.active_players").value(12));

        verify(jackpotService).getTotalJackpots();
        verify(jackpotService).getTotalBetsProcessed();
        verify(jackpotService).getTotalAmountWagered();
        verify(jackpotService).getTotalWins();
        verify(jackpotService).getTotalAmountWon();
        verify(jackpotService).getActivePlayersCount();
    }

    @Test
    void getCustomMetrics_WithZeroValues_ShouldReturnZeroMetrics() throws Exception {
        when(jackpotService.getTotalJackpots()).thenReturn(0L);
        when(jackpotService.getTotalBetsProcessed()).thenReturn(0L);
        when(jackpotService.getTotalAmountWagered()).thenReturn(BigDecimal.ZERO);
        when(jackpotService.getTotalWins()).thenReturn(0L);
        when(jackpotService.getTotalAmountWon()).thenReturn(BigDecimal.ZERO);
        when(jackpotService.getActivePlayersCount()).thenReturn(0L);

        mockMvc.perform(get("/api/metrics/custom"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_jackpots").value(0))
                .andExpect(jsonPath("$.total_bets_processed").value(0))
                .andExpect(jsonPath("$.total_amount_wagered").value(0))
                .andExpect(jsonPath("$.total_wins").value(0))
                .andExpect(jsonPath("$.total_amount_won").value(0))
                .andExpect(jsonPath("$.active_players").value(0));
    }

    @Test
    void getCustomMetrics_WithLargeValues_ShouldReturnCorrectMetrics() throws Exception {
        when(jackpotService.getTotalJackpots()).thenReturn(999L);
        when(jackpotService.getTotalBetsProcessed()).thenReturn(100000L);
        when(jackpotService.getTotalAmountWagered()).thenReturn(new BigDecimal("999999.99"));
        when(jackpotService.getTotalWins()).thenReturn(5000L);
        when(jackpotService.getTotalAmountWon()).thenReturn(new BigDecimal("500000.50"));
        when(jackpotService.getActivePlayersCount()).thenReturn(1000L);

        mockMvc.perform(get("/api/metrics/custom"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_jackpots").value(999))
                .andExpect(jsonPath("$.total_bets_processed").value(100000))
                .andExpect(jsonPath("$.total_amount_wagered").value(999999.99))
                .andExpect(jsonPath("$.total_wins").value(5000))
                .andExpect(jsonPath("$.total_amount_won").value(500000.50))
                .andExpect(jsonPath("$.active_players").value(1000));
    }

    @Test
    void getCustomMetrics_ShouldReturnCorrectJsonStructure() throws Exception {
        when(jackpotService.getTotalJackpots()).thenReturn(1L);
        when(jackpotService.getTotalBetsProcessed()).thenReturn(1L);
        when(jackpotService.getTotalAmountWagered()).thenReturn(new BigDecimal("1.00"));
        when(jackpotService.getTotalWins()).thenReturn(1L);
        when(jackpotService.getTotalAmountWon()).thenReturn(new BigDecimal("1.00"));
        when(jackpotService.getActivePlayersCount()).thenReturn(1L);

        mockMvc.perform(get("/api/metrics/custom"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.total_jackpots").exists())
                .andExpect(jsonPath("$.total_bets_processed").exists())
                .andExpect(jsonPath("$.total_amount_wagered").exists())
                .andExpect(jsonPath("$.total_wins").exists())
                .andExpect(jsonPath("$.total_amount_won").exists())
                .andExpect(jsonPath("$.active_players").exists())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasKey("total_jackpots")))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasKey("total_bets_processed")))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasKey("total_amount_wagered")))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasKey("total_wins")))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasKey("total_amount_won")))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasKey("active_players")));
    }

    @Test
    void getCustomMetrics_ShouldCallServiceMethodsOnce() throws Exception {
        when(jackpotService.getTotalJackpots()).thenReturn(1L);
        when(jackpotService.getTotalBetsProcessed()).thenReturn(1L);
        when(jackpotService.getTotalAmountWagered()).thenReturn(BigDecimal.ONE);
        when(jackpotService.getTotalWins()).thenReturn(1L);
        when(jackpotService.getTotalAmountWon()).thenReturn(BigDecimal.ONE);
        when(jackpotService.getActivePlayersCount()).thenReturn(1L);

        mockMvc.perform(get("/api/metrics/custom"))
                .andExpect(status().isOk());

        verify(jackpotService, org.mockito.Mockito.times(1)).getTotalJackpots();
        verify(jackpotService, org.mockito.Mockito.times(1)).getTotalBetsProcessed();
        verify(jackpotService, org.mockito.Mockito.times(1)).getTotalAmountWagered();
        verify(jackpotService, org.mockito.Mockito.times(1)).getTotalWins();
        verify(jackpotService, org.mockito.Mockito.times(1)).getTotalAmountWon();
        verify(jackpotService, org.mockito.Mockito.times(1)).getActivePlayersCount();
    }
}