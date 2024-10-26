package faang.school.paymentservice.dto.event.dmsevent;

import faang.school.paymentservice.dto.Currency;
import lombok.Data;

@Data
public class DmsEventDto{
    Long requestId;
    Long userId;
    Long receiverId;
    Double amount;
    Currency currency;
    DmsTypeOperation typeOperation;
}
