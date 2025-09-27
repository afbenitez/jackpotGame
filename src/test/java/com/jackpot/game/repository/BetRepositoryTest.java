package com.jackpot.game.repository;

import com.jackpot.game.entity.Bet;
import com.jackpot.game.entity.Jackpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BetRepository betRepository;

    private Jackpot jackpot1;
    private Jackpot jackpot2;
    private Bet bet1;
    private Bet bet2;
    private Bet bet3;

    @BeforeEach
    void setUp() {
        jackpot1 = new Jackpot();
        jackpot1.setName("Test Jackpot 1");
        jackpot1.setCurrentSize(new BigDecimal("100.00"));
        jackpot1.setWinProbability(new BigDecimal("0.1"));
        jackpot1 = entityManager.persistAndFlush(jackpot1);

        jackpot2 = new Jackpot();
        jackpot2.setName("Test Jackpot 2");
        jackpot2.setCurrentSize(new BigDecimal("200.00"));
        jackpot2.setWinProbability(new BigDecimal("0.2"));
        jackpot2 = entityManager.persistAndFlush(jackpot2);

        bet1 = new Bet(jackpot1, "player1", new BigDecimal("10.00"));
        bet1 = entityManager.persistAndFlush(bet1);

        bet2 = new Bet(jackpot1, "player2", new BigDecimal("20.00"));
        bet2 = entityManager.persistAndFlush(bet2);

        bet3 = new Bet(jackpot2, "player1", new BigDecimal("15.00"));
        bet3 = entityManager.persistAndFlush(bet3);
    }

    @Test
    void findByJackpotId_ShouldReturnBetsForSpecificJackpot() {
        List<Bet> betsForJackpot1 = betRepository.findByJackpotId(jackpot1.getId());
        List<Bet> betsForJackpot2 = betRepository.findByJackpotId(jackpot2.getId());

        assertThat(betsForJackpot1).hasSize(2);
        assertThat(betsForJackpot1).extracting(Bet::getPlayerAlias)
                .containsExactlyInAnyOrder("player1", "player2");
        assertThat(betsForJackpot2).hasSize(1);
        assertThat(betsForJackpot2.get(0).getPlayerAlias()).isEqualTo("player1");
    }

    @Test
    void findByJackpotId_WithNonExistentJackpot_ShouldReturnEmptyList() {
        List<Bet> bets = betRepository.findByJackpotId(999L);

        assertThat(bets).isEmpty();
    }

    @Test
    void findByPlayerAlias_ShouldReturnBetsForSpecificPlayer() {
        List<Bet> betsForPlayer1 = betRepository.findByPlayerAlias("player1");
        List<Bet> betsForPlayer2 = betRepository.findByPlayerAlias("player2");

        assertThat(betsForPlayer1).hasSize(2);
        assertThat(betsForPlayer1).extracting(bet -> bet.getJackpot().getName())
                .containsExactlyInAnyOrder("Test Jackpot 1", "Test Jackpot 2");

        assertThat(betsForPlayer2).hasSize(1);
        assertThat(betsForPlayer2.get(0).getJackpot().getName()).isEqualTo("Test Jackpot 1");
    }

    @Test
    void findByPlayerAlias_WithNonExistentPlayer_ShouldReturnEmptyList() {
        List<Bet> bets = betRepository.findByPlayerAlias("nonexistentplayer");
        assertThat(bets).isEmpty();
    }

    @Test
    void sumAllBetAmounts_ShouldReturnTotalOfAllBets() {
        BigDecimal totalAmount = betRepository.sumAllBetAmounts();
        BigDecimal expectedTotal = new BigDecimal("45.00");
        assertThat(totalAmount).isEqualByComparingTo(expectedTotal);
    }

    @Test
    void sumAllBetAmounts_WithNoBets_ShouldReturnZero() {
        betRepository.deleteAll();
        entityManager.flush();

        BigDecimal totalAmount = betRepository.sumAllBetAmounts();

        assertThat(totalAmount).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void countDistinctPlayers_ShouldReturnNumberOfUniquePlayers() {
        long distinctPlayerCount = betRepository.countDistinctPlayers();

        assertThat(distinctPlayerCount).isEqualTo(2L);
    }

    @Test
    void countDistinctPlayers_WithNoBets_ShouldReturnZero() {
        betRepository.deleteAll();
        entityManager.flush();

        long distinctPlayerCount = betRepository.countDistinctPlayers();
        assertThat(distinctPlayerCount).isEqualTo(0L);
    }

    @Test
    void countDistinctPlayers_WithSamePlayerMultipleBets_ShouldCountOnce() {
        Bet additionalBet = new Bet(jackpot1, "player1", new BigDecimal("5.00"));
        entityManager.persistAndFlush(additionalBet);

        long distinctPlayerCount = betRepository.countDistinctPlayers();

        assertThat(distinctPlayerCount).isEqualTo(2L);
    }

    @Test
    void save_ShouldPersistBetWithTimestamp() {
        LocalDateTime beforeSave = LocalDateTime.now();
        Bet newBet = new Bet(jackpot1, "player3", new BigDecimal("25.00"));

        Bet savedBet = betRepository.save(newBet);
        LocalDateTime afterSave = LocalDateTime.now();

        assertThat(savedBet.getId()).isNotNull();
        assertThat(savedBet.getPlayerAlias()).isEqualTo("player3");
        assertThat(savedBet.getBetAmount()).isEqualByComparingTo(new BigDecimal("25.00"));
        assertThat(savedBet.getJackpot()).isEqualTo(jackpot1);
        assertThat(savedBet.getTimestamp()).isBetween(beforeSave, afterSave);
    }

    @Test
    void findAll_ShouldReturnAllBets() {
        List<Bet> allBets = betRepository.findAll();

        assertThat(allBets).hasSize(3);
        assertThat(allBets).extracting(Bet::getPlayerAlias)
                .containsExactlyInAnyOrder("player1", "player2", "player1");
    }
}