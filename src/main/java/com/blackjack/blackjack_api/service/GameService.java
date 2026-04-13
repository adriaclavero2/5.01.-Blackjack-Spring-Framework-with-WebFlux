package com.blackjack.blackjack_api.service;

import com.blackjack.blackjack_api.model.Card;
import com.blackjack.blackjack_api.model.Game;
import com.blackjack.blackjack_api.model.GameStatus;
import com.blackjack.blackjack_api.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
                .playerHand(playerHand)
                .dealerHand(dealerHand)
                .status(GameStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return gameRepository.save(newGame);
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
}
