package com.blackjack.blackjack_api.controller;

import com.blackjack.blackjack_api.dto.PlayerRequest;
import com.blackjack.blackjack_api.model.Player;
import com.blackjack.blackjack_api.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Player> createPlayer(@Valid @RequestBody PlayerRequest request) {

        return playerService.createPlayer(request.getName());
    }
}
