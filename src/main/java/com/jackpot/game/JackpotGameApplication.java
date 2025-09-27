package com.jackpot.game;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Jackpot Game API",
        version = "1.0.0",
        description = "API for managing jackpots and player bets"
    )
)
public class JackpotGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(JackpotGameApplication.class, args);
    }

}
