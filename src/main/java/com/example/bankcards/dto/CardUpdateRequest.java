package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardUpdateRequest {

    @NotNull(message = "Статус карты обязателен")
    private CardStatus status;
}