package com.jackpot.game.repository;

import com.jackpot.game.entity.Jackpot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JackpotRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private JackpotRepository jackpotRepository;
    
    @Test
    void findAll_ShouldReturnAllJackpots() {
        Jackpot jackpot1 = new Jackpot("Jackpot 1", BigDecimal.valueOf(0.1));
        Jackpot jackpot2 = new Jackpot("Jackpot 2", BigDecimal.valueOf(0.2));
        
        entityManager.persist(jackpot1);
        entityManager.persist(jackpot2);
        entityManager.flush();
        
        var result = jackpotRepository.findAll();
        
        assertThat(result).hasSize(2);
        assertThat(result).extracting("name").containsExactlyInAnyOrder("Jackpot 1", "Jackpot 2");
    }
    
    @Test
    void findById_ShouldReturnJackpot() {
        Jackpot jackpot = new Jackpot("Test Jackpot", BigDecimal.valueOf(0.1));
        entityManager.persist(jackpot);
        entityManager.flush();
        Long id = jackpot.getId();
        
        var result = jackpotRepository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Jackpot");
    }
}
