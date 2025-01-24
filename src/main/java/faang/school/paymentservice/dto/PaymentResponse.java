package faang.school.paymentservice.dto;

import java.math.BigDecimal;
public record PaymentResponse(
        Long id,
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        String currency,
        String message
) {
}
