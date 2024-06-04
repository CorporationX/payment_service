package faang.school.paymentservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
