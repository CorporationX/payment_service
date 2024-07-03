package faang.school.paymentservice.dto;

import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.TransactionStatus;

import java.math.BigDecimal;
public record PaymentResponse(
        TransactionStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
