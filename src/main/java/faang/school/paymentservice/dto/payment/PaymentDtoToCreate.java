package faang.school.paymentservice.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentDtoToCreate {

    private UUID idempotencyKey;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private Currency currency;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledAt;
}