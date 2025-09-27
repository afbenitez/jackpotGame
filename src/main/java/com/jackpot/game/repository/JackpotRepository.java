package com.jackpot.game.repository;

import com.jackpot.game.entity.Jackpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JackpotRepository extends JpaRepository<Jackpot, Long> {
    Optional<Jackpot> findByName(String name);
    boolean existsByName(String name);
}
