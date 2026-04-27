package com.blackjack.blackjack_api.controller;

import com.blackjack.blackjack_api.dto.request.NewGameRequest;
import com.blackjack.blackjack_api.dto.request.PlayRequest;
import com.blackjack.blackjack_api.dto.response.GameResponse;
import com.blackjack.blackjack_api.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GameResponse> createNewGame(@RequestBody NewGameRequest request) {
        return gameService.startNewGame(request.getPlayerName())
                .map(GameResponse::fromEntity);
    }

    @GetMapping("/{id}")
    public Mono<GameResponse> getGameDetails(@PathVariable String id) {
        return gameService.getGameById(id)
                .map(GameResponse::fromEntity);
    }

    @PostMapping("/{id}/play")
    public Mono<GameResponse> playGame(@PathVariable String id, @RequestBody PlayRequest request) {
        if ("HIT".equalsIgnoreCase(request.getAction())) {
            return gameService.playerHits(id)
                    .map(GameResponse::fromEntity);
        } else if ("STAND".equalsIgnoreCase(request.getAction())) {
            return gameService.playerStands(id)
                    .map(GameResponse::fromEntity);
        } else {
            return Mono.error(new IllegalArgumentException("Invalid action. Use HIT or STAND."));
        }
    }

    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteGame (@PathVariable String id) {
        return gameService.deleteGame(id);
    }
}