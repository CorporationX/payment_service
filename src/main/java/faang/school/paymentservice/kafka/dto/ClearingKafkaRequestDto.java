package faang.school.paymentservice.kafka.dto;

import java.util.UUID;

public record ClearingKafkaRequestDto(
        UUID senderAccountId,
        UUID recipientAccountId,
        Long amount,
        UUID transferId
) {
}