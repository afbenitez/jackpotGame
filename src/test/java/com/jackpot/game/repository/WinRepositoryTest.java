package com.jackpot.game.repository;

import com.jackpot.game.entity.Jackpot;
import com.jackpot.game.entity.Win;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class WinRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WinRepository winRepository;

    private Jackpot jackpot1;
    private Jackpot jackpot2;
    private Win win1;
    private Win win2;
    private Win win3;
    private Win win4;

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

        win1 = new Win(jackpot1, "alice", new BigDecimal("50.00"));
        win1 = entityManager.persistAndFlush(win1);

        win2 = new Win(jackpot1, "bob", new BigDecimal("75.00"));
        win2 = entityManager.persistAndFlush(win2);

        win3 = new Win(jackpot2, "alice", new BigDecimal("100.00"));
        win3 = entityManager.persistAndFlush(win3);

        win4 = new Win(jackpot1, "charlie", new BigDecimal("25.00"));
        win4 = entityManager.persistAndFlush(win4);
    }

    @Test
    void findWinsWithFilters_WithoutPlayerFilter_ShouldReturnAllWinsOrderedByTimestamp() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Win> result = winRepository.findWinsWithFilters(null, pageable);

        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).extracting(Win::getPlayerAlias)
                .contains("alice", "bob", "charlie");
    }

    @Test
    void findWinsWithFilters_WithPlayerFilter_ShouldReturnMatchingWinsOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Win> result = winRepository.findWinsWithFilters("alice", pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(Win::getPlayerAlias)
                .containsOnly("alice");
    }

    @Test
    void findWinsWithFilters_WithPartialPlayerFilter_ShouldReturnMatchingWins() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Win> result = winRepository.findWinsWithFilters("al", pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Win::getPlayerAlias)
                .containsOnly("alice");
    }

    @Test
    void findWinsWithFilters_WithNonMatchingPlayerFilter_ShouldReturnEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Win> result = winRepository.findWinsWithFilters("nonexistent", pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void findWinsWithFilters_WithPagination_ShouldReturnCorrectPage() {
        Pageable firstPage = PageRequest.of(0, 2); 
        Pageable secondPage = PageRequest.of(1, 2);

        Page<Win> firstPageResult = winRepository.findWinsWithFilters(null, firstPage);
        Page<Win> secondPageResult = winRepository.findWinsWithFilters(null, secondPage);

        assertThat(firstPageResult.getContent()).hasSize(2);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(4);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(2);
        assertThat(firstPageResult.isFirst()).isTrue();
        assertThat(secondPageResult.getContent()).hasSize(2);
        assertThat(secondPageResult.getTotalElements()).isEqualTo(4);
        assertThat(secondPageResult.isLast()).isTrue();
    }

    @Test
    void findWinsWithFilters_WithPlayerFilterAndPagination_ShouldWork() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Win> result = winRepository.findWinsWithFilters("alice", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent().get(0).getPlayerAlias()).isEqualTo("alice");
    }

    @Test
    void sumAllWinAmounts_ShouldReturnTotalOfAllWins() {
        BigDecimal totalAmount = winRepository.sumAllWinAmounts();
        BigDecimal expectedTotal = new BigDecimal("250.00");
        assertThat(totalAmount).isEqualByComparingTo(expectedTotal);
    }

    @Test
    void sumAllWinAmounts_WithNoWins_ShouldReturnZero() {
        winRepository.deleteAll();
        entityManager.flush();

        BigDecimal totalAmount = winRepository.sumAllWinAmounts();

        assertThat(totalAmount).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void save_ShouldPersistWinWithTimestamp() {
        LocalDateTime beforeSave = LocalDateTime.now();
        Win newWin = new Win(jackpot1, "diana", new BigDecimal("125.00"));

        Win savedWin = winRepository.save(newWin);
        LocalDateTime afterSave = LocalDateTime.now();

        assertThat(savedWin.getId()).isNotNull();
        assertThat(savedWin.getPlayerAlias()).isEqualTo("diana");
        assertThat(savedWin.getWinAmount()).isEqualByComparingTo(new BigDecimal("125.00"));
        assertThat(savedWin.getJackpot()).isEqualTo(jackpot1);
        assertThat(savedWin.getTimestamp()).isBetween(beforeSave, afterSave);
    }

    @Test
    void findAll_ShouldReturnAllWins() {
        List<Win> allWins = winRepository.findAll();

        assertThat(allWins).hasSize(4);
        assertThat(allWins).extracting(Win::getPlayerAlias)
                .containsExactlyInAnyOrder("alice", "bob", "alice", "charlie");
    }

    @Test
    void findWinsWithFilters_OrderingByTimestamp_ShouldReturnInDescendingOrder() {
        winRepository.deleteAll();
        entityManager.flush();

        Win oldWin = new Win(jackpot1, "test1", new BigDecimal("10.00"));
        oldWin.setTimestamp(LocalDateTime.now().minusHours(2));
        entityManager.persistAndFlush(oldWin);

        Win newWin = new Win(jackpot1, "test2", new BigDecimal("20.00"));
        newWin.setTimestamp(LocalDateTime.now());
        entityManager.persistAndFlush(newWin);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Win> result = winRepository.findWinsWithFilters(null, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getPlayerAlias()).isEqualTo("test2");
        assertThat(result.getContent().get(1).getPlayerAlias()).isEqualTo("test1");
    }

    @Test
    void findWinsWithFilters_CaseInsensitiveSearch_ShouldWork() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Win> result = winRepository.findWinsWithFilters("alice", pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Win::getPlayerAlias)
                .containsOnly("alice");
    }
}