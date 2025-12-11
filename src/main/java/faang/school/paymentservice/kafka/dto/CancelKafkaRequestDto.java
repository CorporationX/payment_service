package faang.school.paymentservice.kafka.dto;

import java.util.UUID;

public record CancelKafkaRequestDto(
        UUID accountId,
        Long amount,
        UUID transferId
) {
}