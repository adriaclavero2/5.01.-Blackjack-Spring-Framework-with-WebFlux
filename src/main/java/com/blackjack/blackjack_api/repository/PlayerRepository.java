package com.blackjack.blackjack_api.repository;

import com.blackjack.blackjack_api.model.Player;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends ReactiveCrudRepository<Player, String> {
}
