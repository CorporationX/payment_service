package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.ProductCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AuthorizationDto(
        @NotNull
        UUID senderAccountId,

        @NotNull
        UUID recipientAccountId,

        @NotNull
        @Positive
        Long amount,

        @NotNull
        ProductCategory productCategory
) {
}
