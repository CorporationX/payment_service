package faang.school.paymentservice.dto.transfer;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.UUID;

public record TransferRequest(
        @NotNull
        UUID sourceId,

        @NotNull
        UUID targetId,

        @Min(1)
        @NotNull
        BigInteger amount,

        @NotNull
        Currency currency,

        @NotNull
        TransactionType category
) {
}
