package com.blackjack.blackjack_api.dto.request;

import com.blackjack.blackjack_api.model.enums.GameAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlayRequest {
    @NotNull(message = "Action cannot be null. Please use HIT or STAND.")
    private GameAction action;
}
