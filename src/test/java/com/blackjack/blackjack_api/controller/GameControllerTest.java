package com.blackjack.blackjack_api.controller;

import com.blackjack.blackjack_api.model.entities.Game;
import com.blackjack.blackjack_api.model.enums.GameStatus;
import com.blackjack.blackjack_api.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GameController.class)
class GameControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GameService gameService;

    @Test
    void createNewGame_ValidRequest_ReturnsCreated() {
        Game mockGame = Game.builder().id("game-1").playerId("Adria").status(GameStatus.IN_PROGRESS).build();
        when(gameService.startNewGame(anyString())).thenReturn(Mono.just(mockGame));

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"playerId\": \"Adria\"}") // Ajustado al nombre actual
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("game-1")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    void getGameDetails_GameExists_ReturnsOk() {
        Game mockGame = Game.builder().id("game-1").playerId("Adria").build();
        when(gameService.getGameById("game-1")).thenReturn(Mono.just(mockGame));

        webTestClient.get()
                .uri("/game/game-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("game-1");
    }

    @Test
    void playGame_ActionHit_ReturnsGameInProgress() {
        Game mockGame = Game.builder().id("game-1").status(GameStatus.IN_PROGRESS).playerHand(new ArrayList<>()).build();
        when(gameService.playerHits("game-1")).thenReturn(Mono.just(mockGame));

        webTestClient.post()
                .uri("/game/game-1/play")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"action\": \"HIT\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    void deleteGame_GameExists_ReturnsNoContent() {
        when(gameService.deleteGame("game-1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/game/game-1/delete")
                .exchange()
                .expectStatus().isNoContent();
    }
}