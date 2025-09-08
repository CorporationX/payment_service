package faang.school.paymentservice.model.dto;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.model.enums.PaymentMessageType;
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
public class PaymentMessageDto {
    private UUID idempotencyToken;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private Currency currency;
    private LocalDateTime scheduledAt;
    private PaymentMessageType type;
}