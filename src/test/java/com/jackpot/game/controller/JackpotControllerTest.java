package com.jackpot.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jackpot.game.dto.*;
import com.jackpot.game.entity.Jackpot;
import com.jackpot.game.exception.GlobalExceptionHandler;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JackpotControllerTest {

    @Mock
    private JackpotService jackpotService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private JackpotController jackpotController;

    @BeforeEach
    void setUp() {
        jackpotController = new JackpotController(jackpotService);
        mockMvc = MockMvcBuilders.standaloneSetup(jackpotController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createJackpot_WithValidRequest_ShouldReturnSuccessResponse() throws Exception {
        CreateJackpotRequest request = new CreateJackpotRequest("Test Jackpot", new BigDecimal("0.1"));
        Jackpot mockJackpot = new Jackpot();
        mockJackpot.setId(1L);
        mockJackpot.setName("Test Jackpot");
        mockJackpot.setWinProbability(new BigDecimal("0.1"));

        when(jackpotService.createJackpot(anyString(), any(BigDecimal.class))).thenReturn(mockJackpot);

        mockMvc.perform(post("/api/jackpots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jackpotId").value(1L))
                .andExpect(jsonPath("$.message").value("Jackpot created successfully"));
    }

    @Test
    void createJackpot_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        CreateJackpotRequest request = new CreateJackpotRequest();

        mockMvc.perform(post("/api/jackpots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createJackpot_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        CreateJackpotRequest request = new CreateJackpotRequest("", new BigDecimal("0.1"));

        mockMvc.perform(post("/api/jackpots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createJackpot_WithZeroProbability_ShouldReturnBadRequest() throws Exception {
        CreateJackpotRequest request = new CreateJackpotRequest("Test Jackpot", BigDecimal.ZERO);


        mockMvc.perform(post("/api/jackpots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createJackpot_WithProbabilityGreaterThanOne_ShouldReturnBadRequest() throws Exception {
        CreateJackpotRequest request = new CreateJackpotRequest("Test Jackpot", new BigDecimal("1.5"));

        mockMvc.perform(post("/api/jackpots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createJackpot_WithDuplicateName_ShouldReturnConflict() throws Exception {
        CreateJackpotRequest request = new CreateJackpotRequest("Existing Jackpot", new BigDecimal("0.1"));
        
        when(jackpotService.createJackpot(anyString(), any(BigDecimal.class)))
                .thenThrow(new com.jackpot.game.exception.JackpotAlreadyExistsException("A jackpot with name 'Existing Jackpot' already exists"));

        mockMvc.perform(post("/api/jackpots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllJackpots_ShouldReturnListOfJackpots() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        List<JackpotResponse> mockJackpots = Arrays.asList(
                new JackpotResponse(1L, "Jackpot 1", new BigDecimal("100.00"), 5, now),
                new JackpotResponse(2L, "Jackpot 2", new BigDecimal("200.00"), 10, now)
        );

        when(jackpotService.getAllJackpots()).thenReturn(mockJackpots);

        mockMvc.perform(get("/api/jackpots"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].jackpotId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Jackpot 1"))
                .andExpect(jsonPath("$[0].currentSize").value(100.00))
                .andExpect(jsonPath("$[1].jackpotId").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jackpot 2"));
    }

    @Test
    void getAllJackpots_WhenEmpty_ShouldReturnEmptyArray() throws Exception {
        when(jackpotService.getAllJackpots()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/jackpots"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void placeBet_WithValidRequest_ShouldReturnBetResponse() throws Exception {
        Long jackpotId = 1L;
        BetRequest request = new BetRequest("player1", new BigDecimal("10.00"));
        BetResponse mockResponse = new BetResponse(new BigDecimal("100.00"), new BigDecimal("500.00"), true);

        when(jackpotService.placeBet(anyLong(), anyString(), any(BigDecimal.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/jackpots/{id}/bet", jackpotId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.winAmount").value(100.00))
                .andExpect(jsonPath("$.newJackpotSize").value(500.00))
                .andExpect(jsonPath("$.win").value(true));
    }

    @Test
    void placeBet_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        Long jackpotId = 1L;
        BetRequest request = new BetRequest();

        mockMvc.perform(post("/api/jackpots/{id}/bet", jackpotId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_WithEmptyPlayerAlias_ShouldReturnBadRequest() throws Exception {
        Long jackpotId = 1L;
        BetRequest request = new BetRequest("", new BigDecimal("10.00"));

        mockMvc.perform(post("/api/jackpots/{id}/bet", jackpotId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_WithInvalidBetAmount_ShouldReturnBadRequest() throws Exception {
        Long jackpotId = 1L;
        BetRequest request = new BetRequest("player1", new BigDecimal("0.00"));

        mockMvc.perform(post("/api/jackpots/{id}/bet", jackpotId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWins_WithoutParameters_ShouldReturnAllWins() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        List<WinResponse> mockWins = Arrays.asList(
                new WinResponse(1L, "player1", new BigDecimal("100.00"), now, "Jackpot 1"),
                new WinResponse(2L, "player2", new BigDecimal("200.00"), now, "Jackpot 1")
        );

        when(jackpotService.getWins(null, null, null)).thenReturn(mockWins);

        mockMvc.perform(get("/api/wins"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].winId").value(1L))
                .andExpect(jsonPath("$[0].playerAlias").value("player1"))
                .andExpect(jsonPath("$[1].playerAlias").value("player2"));
    }

    @Test
    void getWins_WithPlayerAlias_ShouldReturnFilteredWins() throws Exception {
        String playerAlias = "player1";
        LocalDateTime now = LocalDateTime.now();
        List<WinResponse> mockWins = Collections.singletonList(
                new WinResponse(1L, "player1", new BigDecimal("100.00"), now, "Jackpot 1")
        );

        when(jackpotService.getWins(eq(playerAlias), isNull(), isNull())).thenReturn(mockWins);

        mockMvc.perform(get("/api/wins")
                .param("playerAlias", playerAlias))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].playerAlias").value("player1"));
    }

    @Test
    void getWins_WithPagination_ShouldReturnPaginatedResults() throws Exception {
        Integer page = 0;
        Integer size = 10;
        LocalDateTime now = LocalDateTime.now();
        List<WinResponse> mockWins = Collections.singletonList(
                new WinResponse(1L, "player1", new BigDecimal("100.00"), now, "Jackpot 1")
        );

        when(jackpotService.getWins(isNull(), eq(page), eq(size))).thenReturn(mockWins);

        mockMvc.perform(get("/api/wins")
                .param("page", page.toString())
                .param("size", size.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getWins_WithAllParameters_ShouldReturnFilteredAndPaginatedResults() throws Exception {
        String playerAlias = "player1";
        Integer page = 0;
        Integer size = 5;
        LocalDateTime now = LocalDateTime.now();
        List<WinResponse> mockWins = Collections.singletonList(
                new WinResponse(1L, "player1", new BigDecimal("100.00"), now, "Jackpot 1")
        );

        when(jackpotService.getWins(eq(playerAlias), eq(page), eq(size))).thenReturn(mockWins);

        mockMvc.perform(get("/api/wins")
                .param("playerAlias", playerAlias)
                .param("page", page.toString())
                .param("size", size.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getWins_WhenEmpty_ShouldReturnEmptyArray() throws Exception {
        when(jackpotService.getWins(null, null, null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/wins"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}