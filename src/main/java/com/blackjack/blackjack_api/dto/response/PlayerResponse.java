package com.blackjack.blackjack_api.dto.response;

import com.blackjack.blackjack_api.model.entities.Player;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerResponse {

    private String id;
    private String name;
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private int gamesTied;
    private double winRate;

    public static PlayerResponse fromEntity(Player player) {
        return PlayerResponse.builder()
                .id(player.getId())
                .name(player.getName())
                .gamesPlayed(player.getGamesPlayed())
                .gamesWon(player.getGamesWon())
                .gamesLost(player.getGamesLost())
                .gamesTied(player.getGamesTied())
                .winRate(player.getWinRate())
                .build();
    }
}