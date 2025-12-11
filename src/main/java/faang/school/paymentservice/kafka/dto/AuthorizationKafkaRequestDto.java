package faang.school.paymentservice.kafka.dto;

import java.util.UUID;

public record AuthorizationKafkaRequestDto(
        UUID accountId,
        Long amount,
        UUID transferId
) {
}