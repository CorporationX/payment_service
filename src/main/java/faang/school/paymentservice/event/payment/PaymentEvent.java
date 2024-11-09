package faang.school.paymentservice.event.payment;

import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.PaymentCategory;
import faang.school.paymentservice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private UUID id;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private BigDecimal amount;
    private Currency currency;
    private PaymentStatus status;
    private PaymentCategory category;
    private LocalDateTime clearScheduledAt;
}
