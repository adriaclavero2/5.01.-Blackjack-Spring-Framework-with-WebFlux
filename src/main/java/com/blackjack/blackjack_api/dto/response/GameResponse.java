package com.blackjack.blackjack_api.dto.response;

import com.blackjack.blackjack_api.model.entities.Card;
import com.blackjack.blackjack_api.model.entities.Game;
import com.blackjack.blackjack_api.model.enums.GameStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GameResponse {

    private String id;
    private String playerId;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private GameStatus status;

    public static GameResponse fromEntity(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .playerId(game.getPlayerId())
                .playerHand(game.getPlayerHand())
                .dealerHand(game.getDealerHand())
                .status(game.getStatus())
                .build();
    }
}