package faang.school.paymentservice.event;

import faang.school.paymentservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class CancelPaymentEvent implements Event {
    private Long userId;
    private Long paymentId;
}