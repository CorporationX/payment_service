package faang.school.paymentservice.dto;

import java.math.BigDecimal;
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long userId,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
