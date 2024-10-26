package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationMessage {
    private UUID operationId;
    private UUID accountId;
    private String idempotencyKey;
    private BigDecimal amount;
    private Currency currency;
    private OperationType operationType;
}
