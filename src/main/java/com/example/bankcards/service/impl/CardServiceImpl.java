package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardUpdateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UnauthorizedAccessException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardNumberMasker;
import com.example.bankcards.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;
    private final CardNumberMasker cardNumberMasker;

    @Override
    @Transactional
    public CardDto createCard(CardCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        Card card = new Card();
        card.setCardNumber(encryptionUtil.encrypt(cardNumberMasker.generateCardNumber()));
        card.setCardHolder(request.getCardHolder().toUpperCase());
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setUser(user);

        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }

    @Override
    @Transactional
    public CardDto updateCardStatus(Long cardId, CardUpdateRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        card.setStatus(request.getStatus());
        Card updatedCard = cardRepository.save(card);
        return convertToDto(updatedCard);
    }

    @Override
    @Transactional
    public CardDto blockCard(Long cardId, String username) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (!card.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException();
        }

        card.setStatus(CardStatus.BLOCKED);
        Card updatedCard = cardRepository.save(card);
        return convertToDto(updatedCard);
    }

    @Override
    @Transactional
    public CardDto activateCard(Long cardId, String username) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (!card.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException();
        }

        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            card.setStatus(CardStatus.EXPIRED);
            cardRepository.save(card);
            throw new RuntimeException("Cannot activate expired card");
        }

        card.setStatus(CardStatus.ACTIVE);
        Card updatedCard = cardRepository.save(card);
        return convertToDto(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        cardRepository.delete(card);
    }

    @Override
    public CardDto getCardById(Long cardId, String username) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (card.getExpiryDate().isBefore(LocalDate.now()) &&
                !card.getStatus().equals(CardStatus.EXPIRED)) {
            card.setStatus(CardStatus.EXPIRED);
            cardRepository.save(card);
        }

        if (!card.getUser().getUsername().equals(username) && !username.equals("admin")) {
            throw new UnauthorizedAccessException();
        }

        return convertToDto(card);
    }

    @Override
    public Page<CardDto> getUserCards(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));

        return cardRepository.findByUserId(user.getId(), pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<CardDto> getUserCardsByStatus(String username, CardStatus status, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));

        return cardRepository.findByUserIdAndStatus(user.getId(), status, pageable)
                .map(this::convertToDto);
    }

    @Override
    public List<CardDto> getAllUserCards(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));

        return cardRepository.findByUserId(user.getId(), Pageable.unpaged())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public boolean isCardOwnedByUser(Long cardId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));

        return cardRepository.findByIdAndUserId(cardId, user.getId()).isPresent();
    }

    private CardDto convertToDto(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setCardNumber(cardNumberMasker.maskCardNumber(card.getCardNumber(), encryptionUtil));
        dto.setCardHolder(card.getCardHolder());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());
        dto.setUserId(card.getUser().getId());
        dto.setUsername(card.getUser().getUsername());
        return dto;
    }
}