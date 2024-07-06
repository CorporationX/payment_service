package faang.school.paymentservice.dto.event.payment;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class PaymentEvent {
    private BigInteger requesterNumber;
    private BigInteger receiverNumber;
    private Currency currency;
    private BigDecimal amount;
    private PaymentStatus type;
}
