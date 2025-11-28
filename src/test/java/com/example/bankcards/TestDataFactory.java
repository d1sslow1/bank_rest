package com.example.bankcards;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Фабрика тестовых данных для создания объектов в тестах
 */
public class TestDataFactory {

    public static User createUser(Long id, String username, String role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@bank.com");
        user.setPassword("$2a$12$encryptedPassword");
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public static Card createCard(Long id, User user, CardStatus status, BigDecimal balance) {
        Card card = new Card();
        card.setId(id);
        card.setCardNumber("encrypted-card-number");
        card.setCardHolder("ИВАН ИВАНОВ");
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setStatus(status);
        card.setBalance(balance);
        card.setUser(user);
        card.setCreatedAt(LocalDateTime.now());
        return card;
    }

    public static CardDto createCardDto(Long id, String cardHolder, CardStatus status, BigDecimal balance) {
        CardDto cardDto = new CardDto();
        cardDto.setId(id);
        cardDto.setCardNumber("**** **** **** 1234");
        cardDto.setCardHolder(cardHolder);
        cardDto.setExpiryDate(LocalDate.now().plusYears(3));
        cardDto.setStatus(status);
        cardDto.setBalance(balance);
        cardDto.setUserId(1L);
        cardDto.setUsername("testuser");
        return cardDto;
    }

    public static CardCreateRequest createCardCreateRequest(Long userId, String cardHolder) {
        CardCreateRequest request = new CardCreateRequest();
        request.setUserId(userId);
        request.setCardHolder(cardHolder);
        return request;
    }

    public static TransferRequest createTransferRequest(Long fromCardId, Long toCardId, BigDecimal amount) {
        TransferRequest request = new TransferRequest();
        request.setFromCardId(fromCardId);
        request.setToCardId(toCardId);
        request.setAmount(amount);
        return request;
    }
}