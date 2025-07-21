package faang.school.paymentservice.dto.pending;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.enums.RequestType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Data
public class PendingRequestDto {
    private String operationId;
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
    private String token;
    private RequestType requestType;
    private Map<String, String> requestInputData;
    private String additionalDetails;
}
