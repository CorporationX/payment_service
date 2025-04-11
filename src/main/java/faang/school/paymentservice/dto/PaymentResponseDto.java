package faang.school.paymentservice.dto;

import java.math.BigDecimal;

public record PaymentResponseDto(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        CurrencyDto currency,
        String message
) {
}
