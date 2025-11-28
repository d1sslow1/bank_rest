package com.example.bankcards.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Недостаточно средств на карте");
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}