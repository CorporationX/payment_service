package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.Category;
import faang.school.paymentservice.model.OperationStatus;
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
    private UUID accountFromId;
    private UUID accountToId;
    private String idempotencyKey;
    private BigDecimal amount;
    private Currency currency;
    private Category category;
    private OperationStatus status;
}
