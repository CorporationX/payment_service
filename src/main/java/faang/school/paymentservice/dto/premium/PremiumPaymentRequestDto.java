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
    private PremiumRequestDto premiumRequestDto;
    private PaymentRequestDto paymentRequestDto;
    private CurrencyDto selectedCurrency;
    private boolean byUser;
}

