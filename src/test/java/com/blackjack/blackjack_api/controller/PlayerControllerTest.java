package com.blackjack.blackjack_api.controller;

import com.blackjack.blackjack_api.model.entities.Player;
import com.blackjack.blackjack_api.service.GameService;
import com.blackjack.blackjack_api.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(PlayerController.class)
class PlayerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PlayerService playerService;

    @MockitoBean
    private GameService gameService;

    @Test
    void createPlayer_ValidName_ReturnsCreated() {
        Player mockPlayer = Player.builder().id("1").name("Adrià").gamesPlayed(0).build();
        when(playerService.createPlayer(anyString())).thenReturn(Mono.just(mockPlayer));

        webTestClient.post()
                .uri("/player")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Adrià\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Adrià")
                .jsonPath("$.id").isEqualTo("1");
    }

    @Test
    void getRanking_PlayersExist_ReturnsPlayerList() {
        Player p1 = Player.builder().id("1").name("Adrià").gamesWon(5).build();
        when(playerService.getTopPlayers()).thenReturn(Flux.just(p1));

        webTestClient.get()
                .uri("/ranking")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Adrià")
                .jsonPath("$[0].gamesWon").isEqualTo(5);
    }
}