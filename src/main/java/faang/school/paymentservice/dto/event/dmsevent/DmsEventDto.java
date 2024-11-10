package faang.school.paymentservice.dto.event.dmsevent;

import faang.school.paymentservice.dto.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DmsEventDto{
    Long requestId;
    Long senderId;
    Long receiverId;
    BigDecimal amount;
    Currency currency;
    DmsTypeOperation typeOperation;
    LocalDateTime clearScheduledAt;
}
