package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.model.ProductCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public record BankOperationDto(
        UUID senderAccountId,
        UUID recipientAccountId,
        Long amount,
        ProductCategory productCategory,
        LocalDateTime clearScheduledAt,
        PaymentStatus status
) {
}
