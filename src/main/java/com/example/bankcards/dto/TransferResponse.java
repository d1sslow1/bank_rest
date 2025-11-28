package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransferResponse {
    private Long id;
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime timestamp;

    public TransferResponse(Long fromCardId, Long toCardId, BigDecimal amount, String status, LocalDateTime timestamp) {
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }
}