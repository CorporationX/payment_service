package faang.school.paymentservice.kafka.dto;

import faang.school.paymentservice.model.PaymentStatus;

import java.util.UUID;

public record CancelKafkaResponseDto(
        UUID transferId,
        PaymentStatus paymentStatus,
        String description
) {
}