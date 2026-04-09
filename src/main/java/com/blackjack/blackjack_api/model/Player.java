package com.blackjack.blackjack_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("players")
public class Player {

    @Id
    private String id;

    private String name;

    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private int gamesTied;
    private double winRate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
