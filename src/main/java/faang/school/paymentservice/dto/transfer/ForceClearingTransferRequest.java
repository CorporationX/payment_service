package faang.school.paymentservice.dto.transfer;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ForceClearingTransferRequest(
        @NotNull
        UUID sourceAccountId,

        @NotNull
        UUID transferId
) {
}
