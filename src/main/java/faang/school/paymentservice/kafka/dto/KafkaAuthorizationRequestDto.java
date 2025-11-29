package faang.school.paymentservice.kafka.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record KafkaAuthorizationRequestDto(
        UUID accountId,
        BigDecimal amount,
        UUID operationId
) {
}