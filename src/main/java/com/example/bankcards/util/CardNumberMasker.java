package com.example.bankcards.util;

import org.springframework.stereotype.Component;

@Component
public class CardNumberMasker {

    public String maskCardNumber(String encryptedCardNumber, EncryptionUtil encryptionUtil) {
        try {
            String decrypted = encryptionUtil.decrypt(encryptedCardNumber);
            if (decrypted.length() >= 4) {
                String lastFour = decrypted.substring(decrypted.length() - 4);
                return "**** **** **** " + lastFour;
            }
            return "**** **** **** ****";
        } catch (Exception e) {
            return "**** **** **** ****";
        }
    }

    public String generateCardNumber() {
        // Генерация случайного номера карты (16 цифр)
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append((int) (Math.random() * 10));
        }
        return cardNumber.toString();
    }
}