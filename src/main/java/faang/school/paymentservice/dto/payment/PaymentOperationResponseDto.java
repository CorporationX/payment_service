package faang.school.paymentservice.dto.payment;

import faang.school.paymentservice.entity.payment.PaymentOperationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOperationResponseDto {
    // TODO: не уверен что нужно отдавать оба
    private UUID id;
    private UUID operationToken;
    private UUID accountFromId;
    private UUID accountToId;
    private UUID currencyId;
    private BigDecimal amount;
    private PaymentOperationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime clearScheduledAt;
}
