package faang.school.paymentservice.model.event;

import faang.school.paymentservice.model.enums.OperationType;
import faang.school.paymentservice.model.enums.RequestType;
import lombok.AllArgsConstructor;
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
public class PaymentEvent {
    private UUID idempotencyToken;
    private Long senderAccountId;
    private Long recipientAccountId;
    private RequestType requestType;
    private OperationType operationType;
    private BigDecimal amount;
    private LocalDateTime sentDateTime;
    private Long requestId;
}