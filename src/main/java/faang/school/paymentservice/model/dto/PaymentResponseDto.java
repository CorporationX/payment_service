package faang.school.paymentservice.model.dto;

import faang.school.paymentservice.model.enums.PaymentStages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private UUID idempotencyToken;
    private PaymentStages status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}