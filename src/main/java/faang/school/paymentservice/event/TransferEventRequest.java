package faang.school.paymentservice.event;

import faang.school.paymentservice.dto.Currency;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.UUID;

@Builder
@Getter
@Setter
public class TransferEventRequest {
    private UUID id;
    private Long userId;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private Currency currency;
    private BigInteger withdrawalAmount;
}
