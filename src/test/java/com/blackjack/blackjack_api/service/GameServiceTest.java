package com.blackjack.blackjack_api.service;

import com.blackjack.blackjack_api.model.entities.Card;
import com.blackjack.blackjack_api.model.entities.Game;
import com.blackjack.blackjack_api.model.enums.GameStatus;
import com.blackjack.blackjack_api.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void testStartNewGame_Success() {
        when(gameRepository.save(any(Game.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        Mono<Game> result = gameService.startNewGame("player-123");

        StepVerifier.create(result)
                .expectNextMatches(game ->
                        game.getPlayerId().equals("player-123") &&
                                game.getStatus() == GameStatus.IN_PROGRESS &&
                                game.getPlayerHand().size() == 2 &&
                                game.getDealerHand().size() == 2 &&
                                game.getDeck().size() == 48)
                .verifyComplete();
    }

    @Test
    void testPlayerHits_AndBusts_DealerWins() {
        List<Card> riggedDeck = new ArrayList<>(List.of(new Card("HEARTS", "10", 10)));

        List<Card> playerHand = new ArrayList<>(List.of(
                new Card("SPADES", "10", 10),
                new Card("DIAMONDS", "5", 5)
        ));

        Game game = Game.builder()
                .id("game-1")
                .status(GameStatus.IN_PROGRESS)
                .deck(riggedDeck)
                .playerHand(playerHand)
                .build();

        when(gameRepository.findById("game-1")).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        Mono<Game> result = gameService.playerHits("game-1");

        StepVerifier.create(result)
                .expectNextMatches(updatedGame ->
                        updatedGame.getPlayerHand().size() == 3 &&
                                updatedGame.getStatus() == GameStatus.DEALER_WINS)
                .verifyComplete();
    }

    @Test
    void testPlayerStands_DealerMustHitAndBusts_PlayerWins() {
        List<Card> riggedDeck = new ArrayList<>(List.of(new Card("HEARTS", "10", 10)));

        List<Card> playerHand = new ArrayList<>(List.of(
                new Card("SPADES", "10", 10),
                new Card("DIAMONDS", "10", 10)
        ));

        List<Card> dealerHand = new ArrayList<>(List.of(
                new Card("CLUBS", "10", 10),
                new Card("HEARTS", "6", 6)
        ));

        Game game = Game.builder()
                .id("game-2")
                .status(GameStatus.IN_PROGRESS)
                .deck(riggedDeck)
                .playerHand(playerHand)
                .dealerHand(dealerHand)
                .build();

        when(gameRepository.findById("game-2")).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        Mono<Game> result = gameService.playerStands("game-2");

        StepVerifier.create(result)
                .expectNextMatches(updatedGame ->
                        updatedGame.getDealerHand().size() == 3 &&
                                updatedGame.getStatus() == GameStatus.PLAYER_WINS)
                .verifyComplete();
    }

    @Test
    void testPlayerHits_WhenGameIsFinished_ThrowsError() {
        Game game = Game.builder()
                .id("game-3")
                .status(GameStatus.PLAYER_WINS)
                .build();

        when(gameRepository.findById("game-3")).thenReturn(Mono.just(game));

        Mono<Game> result = gameService.playerHits("game-3");

        StepVerifier.create(result)
                .expectErrorMessage("Game is already finished.")
                .verify();
    }
}