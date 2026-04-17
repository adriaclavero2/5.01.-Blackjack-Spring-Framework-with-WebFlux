package com.blackjack.blackjack_api.service;

import com.blackjack.blackjack_api.model.Player;
import com.blackjack.blackjack_api.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void testCreatePlayer_Success() {
        when(playerRepository.save(any(Player.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Player> result = playerService.createPlayer("Adrià");

        StepVerifier.create(result)
                .expectNextMatches(player ->
                        player.getName().equals("Adrià") &&
                                player.getGamesPlayed() == 0 &&
                                player.getWinRate() == 0.0 &&
                                player.getId() != null)
                .verifyComplete();
    }

    @Test
    void testGetTopPlayers_Success() {
        Player p1 = Player.builder().id("1").name("Adrià").gamesWon(5).build();
        Player p2 = Player.builder().id("2").name("Bot").gamesWon(2).build();
        when(playerRepository.findAllByOrderByGamesWonDesc()).thenReturn(Flux.just(p1, p2));

        Flux<Player> result = playerService.getTopPlayers();

        StepVerifier.create(result)
                .expectNextMatches(player -> player.getName().equals("Adrià") && player.getGamesWon() == 5)
                .expectNextMatches(player -> player.getName().equals("Bot") && player.getGamesWon() == 2)
                .verifyComplete();
    }

    @Test
    void testUpdatePlayerName_Success() {
        String fakeId = UUID.randomUUID().toString();
        Player oldPlayer = Player.builder().id(fakeId).name("OldName").updatedAt(LocalDateTime.now()).build();

        when(playerRepository.findById(fakeId)).thenReturn(Mono.just(oldPlayer));
        when(playerRepository.save(any(Player.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        Mono<Player> result = playerService.updatePlayerName(fakeId, "NewName");

        StepVerifier.create(result)
                .expectNextMatches(player -> player.getName().equals("NewName"))
                .verifyComplete();
    }

    @Test
    void testUpdatePlayerName_NotFound() {
        String fakeId = "id-inventado";
        when(playerRepository.findById(fakeId)).thenReturn(Mono.empty());

        Mono<Player> result = playerService.updatePlayerName(fakeId, "NewName");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                        ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND))
                .verify();
    }
}
