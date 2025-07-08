package faang.school.paymentservice.dto.transfer;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.TransferStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TransferResponse(
        @NotNull
        UUID transferId,

        @NotNull
        TransferStatus transferStatus,

        @NotNull
        PaymentStatus paymentStatus
) {
}
