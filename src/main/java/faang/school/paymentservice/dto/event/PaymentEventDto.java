package faang.school.paymentservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class PaymentEventDto extends EventDto implements Serializable {
    private Long paymentNumber;
    private Long debitAccountId;
    private Long creditAccountId;
    private BigDecimal amount;
    private String type;
    private String currency;
}
