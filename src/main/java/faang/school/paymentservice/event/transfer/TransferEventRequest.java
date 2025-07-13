package faang.school.paymentservice.event.transfer;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.UUID;

@Builder
@Getter
@Setter
public class TransferEventRequest {
    @NotNull
    private UUID authorizationId;
    @NotNull
    private Long userId;
    @NotNull
    private UUID sourceId;
    @NotNull
    private UUID targetId;
    @NotNull
    private Currency currency;
    @NotNull
    private BigInteger amount;
    @NotNull
    private TransactionType category;
}
