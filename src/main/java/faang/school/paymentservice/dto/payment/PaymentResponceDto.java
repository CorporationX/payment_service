package faang.school.paymentservice.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponceDto {
    private UUID id;
    private String amount;
    private Currency currency;
    private UUID accountFromId;
    private UUID accountToId;
    private PaymentStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime clearScheduledAt;
}
