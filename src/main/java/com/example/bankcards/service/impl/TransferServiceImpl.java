package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.exception.UnauthorizedAccessException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;

    @Override
    @Transactional
    public TransferResponse transferBetweenOwnCards(TransferRequest request, String username) {
        validateTransfer(request, username);

        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new CardNotFoundException(request.getFromCardId()));
        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new CardNotFoundException(request.getToCardId()));

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException();
        }

        try {
            fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
            toCard.setBalance(toCard.getBalance().add(request.getAmount()));

            cardRepository.save(fromCard);
            cardRepository.save(toCard);

            Transfer transfer = new Transfer();
            transfer.setFromCard(fromCard);
            transfer.setToCard(toCard);
            transfer.setAmount(request.getAmount());
            transfer.setStatus("COMPLETED");

            Transfer savedTransfer = transferRepository.save(transfer);

            return new TransferResponse(
                    savedTransfer.getId(),
                    fromCard.getId(),
                    toCard.getId(),
                    request.getAmount(),
                    "COMPLETED",
                    savedTransfer.getCreatedAt()
            );

        } catch (Exception e) {
            throw new TransferException("Transfer error: " + e.getMessage());
        }
    }

    @Override
    public void validateTransfer(TransferRequest request, String username) {
        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new CardNotFoundException(request.getFromCardId()));
        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new CardNotFoundException(request.getToCardId()));

        // Проверяем не истекли ли сроки действия карт
        if (fromCard.getExpiryDate().isBefore(LocalDate.now())) {
            fromCard.setStatus(CardStatus.EXPIRED);
            cardRepository.save(fromCard);
            throw new TransferException("Sender card has expired");
        }

        if (toCard.getExpiryDate().isBefore(LocalDate.now())) {
            toCard.setStatus(CardStatus.EXPIRED);
            cardRepository.save(toCard);
            throw new TransferException("Receiver card has expired");
        }

        if (!fromCard.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Sender card does not belong to user");
        }

        if (!toCard.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Receiver card does not belong to user");
        }

        if (!fromCard.getStatus().equals(CardStatus.ACTIVE)) {
            throw new TransferException("Sender card is not active");
        }

        if (!toCard.getStatus().equals(CardStatus.ACTIVE)) {
            throw new TransferException("Receiver card is not active");
        }

        if (fromCard.getId().equals(toCard.getId())) {
            throw new TransferException("Cannot transfer to the same card");
        }

        if (request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new TransferException("Transfer amount must be positive");
        }
    }
}