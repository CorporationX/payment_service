package faang.school.paymentservice.dto.transfer;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelTransferRequest(
        @NotNull
        UUID transferId
) {
}
