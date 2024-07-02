package faang.school.paymentservice.dto.transaction;

import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransactionDto {

    private Long id;
    private UUID idempotencyKey;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private Currency currency;
    private BigDecimal amount;
    private TransactionStatus transactionStatus;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
}