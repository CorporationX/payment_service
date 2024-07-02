package faang.school.paymentservice.event;

import faang.school.paymentservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class NewTransactionEvent implements Event {
    private Long senderBalanceId;
    private Long receiverBalanceId;
    private Currency currency;
    private BigDecimal amount;
}