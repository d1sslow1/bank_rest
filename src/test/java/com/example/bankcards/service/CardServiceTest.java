package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UnauthorizedAccessException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.CardServiceImpl;
import com.example.bankcards.util.CardNumberMasker;
import com.example.bankcards.util.EncryptionUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EncryptionUtil encryptionUtil;

    @Mock
    private CardNumberMasker cardNumberMasker;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void createCard_ShouldReturnCardDto() {
        CardCreateRequest request = new CardCreateRequest();
        request.setUserId(1L);
        request.setCardHolder("ИВАН ИВАНОВ");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardNumberMasker.generateCardNumber()).thenReturn("1234567890123456");
        when(encryptionUtil.encrypt(any())).thenReturn("encrypted");
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardDto result = cardService.createCard(request);

        assertNotNull(result);
        assertEquals("ИВАН ИВАНОВ", result.getCardHolder());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void blockCard_WhenUserOwnsCard_ShouldBlockCard() {
        User user = new User();
        user.setUsername("testuser");

        Card card = new Card();
        card.setId(1L);
        card.setUser(user);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        CardDto result = cardService.blockCard(1L, "testuser");

        assertNotNull(result);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
    }

    @Test
    void getCardById_WhenCardNotFound_ShouldThrowException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> {
            cardService.getCardById(1L, "testuser");
        });
    }
}