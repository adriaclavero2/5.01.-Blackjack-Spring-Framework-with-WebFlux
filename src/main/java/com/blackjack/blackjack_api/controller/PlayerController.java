package com.blackjack.blackjack_api.controller;

import com.blackjack.blackjack_api.dto.PlayerRequest;
import com.blackjack.blackjack_api.model.Player;
import com.blackjack.blackjack_api.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/player")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Player> createPlayer(@Valid @RequestBody PlayerRequest request) {
        return playerService.createPlayer(request.getName());
    }

    @GetMapping("/ranking")
    public Flux<Player> getRanking() {
        return playerService.getTopPlayers();
    }

    @PutMapping("/player/{playerId}")
    public Mono<Player> updatePlayerName(@PathVariable String playerId, @Valid @RequestBody PlayerRequest request) {
        return playerService.updatePlayerName(playerId, request.getName());
    }
}
