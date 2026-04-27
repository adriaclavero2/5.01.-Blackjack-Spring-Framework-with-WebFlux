package com.blackjack.blackjack_api.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("players")
public class Player implements Persistable<String> {

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

    @Transient
    @Builder.Default
    private boolean isNewObject = true;

    @Override
    @Transient
    public boolean isNew() {
        return this.isNewObject || this.id == null;
    }
}
