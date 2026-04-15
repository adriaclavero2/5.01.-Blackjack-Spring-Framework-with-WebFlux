package com.blackjack.blackjack_api.controller;

import com.blackjack.blackjack_api.dto.NewGameRequest;
import com.blackjack.blackjack_api.dto.PlayRequest;
import com.blackjack.blackjack_api.model.Game;
import com.blackjack.blackjack_api.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Game> createNewGame(@RequestBody NewGameRequest request) {
        return gameService.startNewGame(request.getPlayerName());
    }

    @GetMapping("/{id}")
    public Mono<Game> getGameDetails(@PathVariable String id) {
        return gameService.getGameById(id);
    }

    @PostMapping("/{id}/play")
    public Mono<Game> playGame(@PathVariable String id, @RequestBody PlayRequest request) {
        if ("HIT".equalsIgnoreCase(request.getAction())) {
            return gameService.playerHits(id);
        } else if ("STAND".equalsIgnoreCase(request.getAction())) {
            return gameService.playerStands(id);
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
