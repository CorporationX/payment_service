package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.PaymentType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorizationMessageRequest(
        UUID id,
        String senderAccountNumber,
        String receiverAccountNumber,
        Currency currency,
        BigDecimal amount,
        PaymentType paymentType
) {
}
