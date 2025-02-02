package faang.school.paymentservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        UUID paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
