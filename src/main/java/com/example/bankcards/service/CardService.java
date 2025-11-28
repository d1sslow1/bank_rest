package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardUpdateRequest;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {
    CardDto createCard(CardCreateRequest request);
    CardDto updateCardStatus(Long cardId, CardUpdateRequest request);
    CardDto blockCard(Long cardId, String username);
    CardDto activateCard(Long cardId, String username);
    void deleteCard(Long cardId);
    CardDto getCardById(Long cardId, String username);
    Page<CardDto> getUserCards(String username, Pageable pageable);
    Page<CardDto> getUserCardsByStatus(String username, CardStatus status, Pageable pageable);
    List<CardDto> getAllUserCards(String username);
    Page<CardDto> getAllCards(Pageable pageable);
    boolean isCardOwnedByUser(Long cardId, String username);
}