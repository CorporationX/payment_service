package faang.school.paymentservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NewPaymentEvent implements Event {
    private Long userId;
    private Long paymentId;
}