package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;

public interface TransferService {
    TransferResponse transferBetweenOwnCards(TransferRequest request, String username);
    void validateTransfer(TransferRequest request, String username);
}