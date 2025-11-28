package com.example.bankcards.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("Доступ запрещен");
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}