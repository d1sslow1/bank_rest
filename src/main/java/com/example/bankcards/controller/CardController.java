package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardUpdateRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Page<CardDto>> getUserCards(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) CardStatus status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CardDto> cards;

        if (status != null) {
            cards = cardService.getUserCardsByStatus(userDetails.getUsername(), status, pageable);
        } else {
            cards = cardService.getUserCards(userDetails.getUsername(), pageable);
        }

        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDto> getCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal UserDetails userDetails) {

        CardDto card = cardService.getCardById(cardId, userDetails.getUsername());
        return ResponseEntity.ok(card);
    }

    @PutMapping("/{cardId}/block")
    public ResponseEntity<CardDto> blockCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal UserDetails userDetails) {

        CardDto card = cardService.blockCard(cardId, userDetails.getUsername());
        return ResponseEntity.ok(card);
    }

    @PutMapping("/{cardId}/activate")
    public ResponseEntity<CardDto> activateCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal UserDetails userDetails) {

        CardDto card = cardService.activateCard(cardId, userDetails.getUsername());
        return ResponseEntity.ok(card);
    }

    @PutMapping("/{cardId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> updateCardStatus(
            @PathVariable Long cardId,
            @Valid @RequestBody CardUpdateRequest request) {

        CardDto card = cardService.updateCardStatus(cardId, request);
        return ResponseEntity.ok(card);
    }
}