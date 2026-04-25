package com.blackjack.blackjack_api.repository;

import com.blackjack.blackjack_api.model.Game;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface GameRepository extends ReactiveCrudRepository<Game, String> {
    Flux<Game> findAllByPlayerId(String playerId);
}
