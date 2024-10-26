package faang.school.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingOperationDto {
    private UUID accountId;
    private String idempotencyKey;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime clearScheduledAt;
}
