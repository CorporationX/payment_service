package faang.school.paymentservice.model.event;

import faang.school.paymentservice.model.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    private UUID idempotencyToken;
    private Long senderContextUserId;
    private Long senderAccountId;
    private Long recipientAccountId;
    private OperationType operationType;
    private BigDecimal amount;
    private LocalDateTime sentDateTime;
}