package faang.school.paymentservice.dto.payment;

import faang.school.paymentservice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEventDto {
    private UUID paymentId;
    private String amount;
    private PaymentStatus status;
    private UUID accountFromId;
    private UUID accountToId;
}
