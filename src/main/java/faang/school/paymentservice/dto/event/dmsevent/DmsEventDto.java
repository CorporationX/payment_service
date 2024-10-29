package faang.school.paymentservice.dto.event.dmsevent;

import faang.school.paymentservice.dto.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DmsEventDto{
    Long requestId;
    Long senderId;
    Long receiverId;
    BigDecimal amount;
    Currency currency;
    DmsTypeOperation typeOperation;
    LocalDateTime clearScheduledAt;
}
