package faang.school.paymentservice.kafka.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record KafkaClearingRequestDto(
        UUID senderAccountId,
        UUID recipientAccountId,
        BigDecimal amount,
        UUID operationId
) {
}