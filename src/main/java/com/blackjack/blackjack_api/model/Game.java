package com.blackjack.blackjack_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "games")
public class Game {

    @Id
    private String id;

    private String playerId;

    @Builder.Default
    private List<Card> deck = new ArrayList<>();

    @Builder.Default
    private List<Card> playerHand = new ArrayList<>();

    @Builder.Default
    private List<Card> dealerHand = new ArrayList<>();

    private GameStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
