package faang.school.paymentservice.dto;

import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.PaymentStatus;

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
