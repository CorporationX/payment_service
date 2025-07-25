package faang.school.paymentservice.dto.pending;

import faang.school.paymentservice.dto.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseClearingDto {
    private String accountNumber;
    private Long recipientId;
    private BigDecimal balance;
    private Currency currency;
    private String operationId;
    private boolean isForced;
}
