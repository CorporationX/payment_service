package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.Currency;

import java.math.BigDecimal;
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
