package com.blackjack.blackjack_api.service;

import com.blackjack.blackjack_api.model.enums.GameStatus;
import com.blackjack.blackjack_api.model.entities.Player;
import com.blackjack.blackjack_api.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Mono<Player> createPlayer(String name) {

        Player newPlayer = Player.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .gamesPlayed(0)
                .gamesWon(0)
                .gamesLost(0)
                .gamesTied(0)
                .winRate(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isNewObject(true)
                .build();

        return playerRepository.save(newPlayer);
    }

    public Flux<Player> getTopPlayers() {
        return playerRepository.findAllByOrderByGamesWonDesc();
    }

    public Mono<Player> updatePlayerName(String id, String newName) {
        return playerRepository.findById(id)
                .switchIfEmpty(Mono.error(new org.springframework.web.server
                        .ResponseStatusException(org.springframework
                        .http.HttpStatus.NOT_FOUND, "Player doesn't exist!")))
                .flatMap(player -> {
                    player.setNewObject(false);

                    player.setName(newName);
                    player.setUpdatedAt(LocalDateTime.now());
                    return playerRepository.save(player);
                });
    }

    public Mono<Player> updatePlayerStats(String playerId, GameStatus result) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setNewObject(false);

                    player.setGamesPlayed(player.getGamesPlayed() + 1);

                    if (result == GameStatus.PLAYER_WINS) {
                        player.setGamesWon(player.getGamesWon() + 1);
                    } else if (result == GameStatus.DEALER_WINS) {
                        player.setGamesLost(player.getGamesLost() + 1);
                    } else if (result == GameStatus.TIE) {
                        player.setGamesTied(player.getGamesTied() + 1);
                    }

                    double winRate = (double) player.getGamesWon() / player.getGamesPlayed() * 100;
                    player.setWinRate(Math.round(winRate * 100.0) / 100.0);
                    player.setUpdatedAt(LocalDateTime.now());

                    return playerRepository.save(player);
                });
    }

    public Mono<Player> getPlayerById(String id) {
        return playerRepository.findById(id);
    }
}
