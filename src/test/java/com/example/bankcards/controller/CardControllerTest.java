package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CardController.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @Test
    @WithMockUser(username = "testuser")
    void getUserCards_ShouldReturnCards() throws Exception {
        // Given
        CardDto cardDto = new CardDto();
        cardDto.setId(1L);
        cardDto.setCardNumber("**** **** **** 1234");
        cardDto.setCardHolder("ИВАН ИВАНОВ");
        cardDto.setBalance(BigDecimal.valueOf(1000.00));

        Page<CardDto> cardPage = new PageImpl<>(Collections.singletonList(cardDto));

        when(cardService.getUserCards(eq("testuser"), any(PageRequest.class))).thenReturn(cardPage);

        // When & Then
        mockMvc.perform(get("/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].cardHolder").value("ИВАН ИВАНОВ"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void blockCard_ShouldReturnBlockedCard() throws Exception {
        // Given
        CardDto cardDto = new CardDto();
        cardDto.setId(1L);
        cardDto.setStatus(CardStatus.BLOCKED);

        when(cardService.blockCard(1L, "testuser")).thenReturn(cardDto);

        // When & Then
        mockMvc.perform(put("/cards/1/block")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ЗАБЛОКИРОВАНА"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCard_ShouldReturnCard() throws Exception {
        // Given
        CardDto cardDto = new CardDto();
        cardDto.setId(1L);
        cardDto.setCardNumber("**** **** **** 1234");
        cardDto.setCardHolder("ИВАН ИВАНОВ");

        when(cardService.getCardById(1L, "testuser")).thenReturn(cardDto);

        // When & Then
        mockMvc.perform(get("/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cardHolder").value("ИВАН ИВАНОВ"));
    }
}