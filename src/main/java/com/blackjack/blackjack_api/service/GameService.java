package com.blackjack.blackjack_api.service;

import com.blackjack.blackjack_api.model.Card;
import com.blackjack.blackjack_api.model.Game;
import com.blackjack.blackjack_api.model.GameStatus;
import com.blackjack.blackjack_api.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;

   private static final String[] SUITS = {"HEARTS", "DIAMONDS", "CLUBS", "SPADES"};
   private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
   private static final int[] VALUES = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};

    private List<Card> createAndShuffleDeck() {
        List<Card> deck = Arrays.stream(SUITS)
                .flatMap(suit -> IntStream.range(0, RANKS.length)
                        .mapToObj(rankIndex -> new Card(suit, RANKS[rankIndex], VALUES[rankIndex])))
                .collect(Collectors.toList());

        Collections.shuffle(deck);
        return deck;
    }

    public Mono<Game> startNewGame(String playerId) {
        List<Card> deck = createAndShuffleDeck();

        List<Card> playerHand = new ArrayList<>();
        List<Card> dealerHand = new ArrayList<>();

        playerHand.add(drawCard(deck));
        playerHand.add(drawCard(deck));

        dealerHand.add(drawCard(deck));
        dealerHand.add(drawCard(deck));

        Game newGame = Game.builder()
                .playerId(playerId)
                .deck(deck)
                .playerHand(playerHand)
                .dealerHand(dealerHand)
                .status(GameStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return gameRepository.save(newGame);
    }

    public Mono<Game> playerHits(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (game.getStatus() != GameStatus.IN_PROGRESS) {
                        return Mono.error(new RuntimeException("Game is already finished."));
                    }

                    game.getPlayerHand().add(drawCard(game.getDeck()));

                    int playerScore = calculateHandScore(game.getPlayerHand());
                    
                    if (playerScore > 21) {
                        game.setStatus(GameStatus.DEALER_WINS);
                    }

                    game.setUpdatedAt(LocalDateTime.now());

                    return gameRepository.save(game)
                            .flatMap(savedGame -> {
                                if (savedGame.getStatus() != GameStatus.IN_PROGRESS) {
                                    return playerService.updatePlayerStats(savedGame.getPlayerId(),
                                                    savedGame.getStatus())
                                            .thenReturn(savedGame);
                                }
                                return Mono.just(savedGame);
                            });
                });
    }

    public Mono<Game> playerStands(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (game.getStatus() != GameStatus.IN_PROGRESS) {
                        return Mono.error(new RuntimeException("Game is already finished."));
                    }

                    int dealerScore = calculateHandScore(game.getDealerHand());

                    while (dealerScore < 17) {
                        game.getDealerHand().add(drawCard(game.getDeck()));
                        dealerScore = calculateHandScore(game.getDealerHand());
                    }

                    int playerScore = calculateHandScore(game.getPlayerHand());

                    if (dealerScore > 21) {
                        game.setStatus(GameStatus.PLAYER_WINS);
                    } else if (dealerScore > playerScore) {
                        game.setStatus(GameStatus.DEALER_WINS);
                    } else if (dealerScore < playerScore) {
                        game.setStatus(GameStatus.PLAYER_WINS);
                    } else {
                        game.setStatus(GameStatus.TIE);
                    }

                    game.setUpdatedAt(LocalDateTime.now());
                    return gameRepository.save(game)
                            .flatMap(savedGame -> playerService
                                    .updatePlayerStats(savedGame.getPlayerId(), savedGame.getStatus())
                            .thenReturn(savedGame));
                });
    }

    private Card drawCard(List<Card> deck) {
        return deck.removeFirst();
    }

    private int calculateHandScore(List<Card> hand) {
        int totalScore = hand.stream()
                .mapToInt(Card::getValue)
                .sum();

        long acesCount = hand.stream()
                .filter(card -> "A".equals(card.getRank()))
                .count();

        while (totalScore > 21 && acesCount > 0) {
            totalScore -= 10;
            acesCount--;
        }

        return totalScore;
    }

    public Mono<Game> getGameById(String gameId) {
        return gameRepository.findById(gameId);
    }

    public Mono<Void> deleteGame(String gameId) {
        return gameRepository.deleteById(gameId);
    }

    public Flux<Game> getGamesByPlayerId(String playerId) { return gameRepository.findAllByPlayerId(playerId); }
}
