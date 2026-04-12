package com.blackjack.blackjack_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlayerRequest {

    @NotBlank(message = "Player's name cannot be empty")
    private String name;

}
