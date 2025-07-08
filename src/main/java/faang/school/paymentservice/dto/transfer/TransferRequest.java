package faang.school.paymentservice.dto.transfer;

import faang.school.paymentservice.dto.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.UUID;

public record TransferRequest(
        @NotNull
        UUID sourceAccountId,

        @NotNull
        UUID targetAccountId,

        @Min(1)
        @NotNull
        BigInteger withdrawalAmount,

        @NotNull
        Currency currency
) {
}
