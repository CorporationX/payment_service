package faang.school.paymentservice.dto.payment;

import faang.school.paymentservice.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentDtoToCreate {

    private UUID idempotencyKey;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private Currency currency;
    private BigDecimal amount;
}