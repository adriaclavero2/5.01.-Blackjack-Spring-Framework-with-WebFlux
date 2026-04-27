package com.blackjack.blackjack_api.controller;

import com.blackjack.blackjack_api.dto.request.PlayerRequest;
import com.blackjack.blackjack_api.dto.response.GameResponse;
import com.blackjack.blackjack_api.dto.response.PlayerResponse;
import com.blackjack.blackjack_api.service.GameService;
import com.blackjack.blackjack_api.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final GameService gameService;

    @PostMapping("/player")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
        return playerService.createPlayer(request.getName())
                .map(PlayerResponse::fromEntity); // <- Aquí transformamos el dato antes de sacarlo
    }

    @GetMapping("/ranking")
    public Flux<PlayerResponse> getRanking() {
        return playerService.getTopPlayers()
                .map(PlayerResponse::fromEntity);
    }

    @PutMapping("/player/{playerId}")
    public Mono<PlayerResponse> updatePlayerName(@PathVariable String playerId, @Valid @RequestBody PlayerRequest request) {
        return playerService.updatePlayerName(playerId, request.getName())
                .map(PlayerResponse::fromEntity);
    }

    @GetMapping("/player/{playerId}/games")
    public Flux<GameResponse> getPlayerGames(@PathVariable String playerId) {
        return gameService.getGamesByPlayerId(playerId)
                .map(GameResponse::fromEntity);
    }
}