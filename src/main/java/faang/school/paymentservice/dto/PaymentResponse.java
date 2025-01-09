package faang.school.paymentservice.dto;

import java.math.BigDecimal;
public record PaymentResponse(
        PaymentStatus status,
        String verificationCode,
        String paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
