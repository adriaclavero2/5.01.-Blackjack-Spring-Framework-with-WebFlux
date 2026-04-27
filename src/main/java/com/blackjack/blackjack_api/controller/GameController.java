package com.blackjack.blackjack_api.controller;

import com.blackjack.blackjack_api.dto.request.NewGameRequest;
import com.blackjack.blackjack_api.dto.request.PlayRequest;
import com.blackjack.blackjack_api.dto.response.GameResponse;
import com.blackjack.blackjack_api.service.GameService;
import jakarta.validation.Valid;
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
        return gameService.startNewGame(request.getPlayerId())
                .map(GameResponse::fromEntity);
    }

    @GetMapping("/{id}")
    public Mono<GameResponse> getGameDetails(@PathVariable String id) {
        return gameService.getGameById(id)
                .map(GameResponse::fromEntity);
    }


    @PostMapping("/{id}/play")
    public Mono<GameResponse> playGame(@PathVariable String id, @Valid @RequestBody PlayRequest request) {
        return switch (request.getAction()) {
            case HIT -> gameService.playerHits(id).map(GameResponse::fromEntity);
            case STAND -> gameService.playerStands(id).map(GameResponse::fromEntity);
        };
    }

    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteGame (@PathVariable String id) {
        return gameService.deleteGame(id);
    }
}