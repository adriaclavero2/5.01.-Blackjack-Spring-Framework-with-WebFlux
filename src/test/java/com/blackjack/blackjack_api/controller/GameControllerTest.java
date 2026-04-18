package com.blackjack.blackjack_api.controller;

import com.blackjack.blackjack_api.model.Game;
import com.blackjack.blackjack_api.model.GameStatus;
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
    void testCreateNewGame_Returns201() {
        Game mockGame = Game.builder().id("game-1").playerId("Adria").status(GameStatus.IN_PROGRESS).build();
        when(gameService.startNewGame(anyString())).thenReturn(Mono.just(mockGame));

        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"playerName\": \"Adria\"}") // Simulamos el JSON de entrada
                .exchange()
                .expectStatus().isCreated() // Esperamos un 201 Created
                .expectBody()
                .jsonPath("$.id").isEqualTo("game-1")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    void testGetGameDetails_Returns200() {
        // GIVEN: El servicio encuentra la partida
        Game mockGame = Game.builder().id("game-1").playerId("Adria").build();
        when(gameService.getGameById("game-1")).thenReturn(Mono.just(mockGame));

        // WHEN & THEN: Hacemos GET a /game/{id}
        webTestClient.get()
                .uri("/game/game-1")
                .exchange()
                .expectStatus().isOk() // Esperamos un 200 OK
                .expectBody()
                .jsonPath("$.id").isEqualTo("game-1");
    }

    @Test
    void testPlayGame_Hit_Returns200() {
        // GIVEN: El servicio procesa pedir carta (HIT)
        Game mockGame = Game.builder().id("game-1").status(GameStatus.IN_PROGRESS).playerHand(new ArrayList<>()).build();
        when(gameService.playerHits("game-1")).thenReturn(Mono.just(mockGame));

        // WHEN & THEN: Hacemos POST a /game/{id}/play pidiendo carta
        webTestClient.post()
                .uri("/game/game-1/play")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"action\": \"HIT\"}")
                .exchange()
                .expectStatus().isOk() // Esperamos un 200 OK
                .expectBody()
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    void testDeleteGame_Returns204() {
        // GIVEN: El servicio borra la partida sin devolver nada (Mono.empty)
        when(gameService.deleteGame("game-1")).thenReturn(Mono.empty());

        // WHEN & THEN: Hacemos DELETE a /game/{id}/delete
        webTestClient.delete()
                .uri("/game/game-1/delete")
                .exchange()
                .expectStatus().isNoContent(); // Esperamos un 204 No Content
    }
}