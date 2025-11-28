package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardCreateRequest {

    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @NotBlank(message = "Имя владельца карты обязательно")
    private String cardHolder;
}