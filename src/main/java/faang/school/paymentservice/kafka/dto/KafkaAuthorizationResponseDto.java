package faang.school.paymentservice.kafka.dto;

import faang.school.paymentservice.model.PaymentStatus;

import java.util.UUID;

public record KafkaAuthorizationResponseDto(
        UUID operationId,
        PaymentStatus paymentStatus,
        String description
) {
}