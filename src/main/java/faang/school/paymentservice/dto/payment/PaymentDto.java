package faang.school.paymentservice.dto.payment;

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
public class PaymentDto {

    private UUID id;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String category;
    private LocalDateTime clearScheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
