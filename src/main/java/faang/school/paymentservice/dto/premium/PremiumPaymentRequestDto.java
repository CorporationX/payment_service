package faang.school.paymentservice.dto.premium;

import faang.school.paymentservice.dto.CurrencyDto;
import faang.school.paymentservice.dto.PaymentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumPaymentRequestDto {
    private PremiumRequestDto premiumRequest;
    private PaymentRequestDto paymentRequest;
    private CurrencyDto selectedCurrency;
    private boolean byUser;
}

