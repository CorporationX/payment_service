package faang.school.paymentservice.dto.premium;

import faang.school.paymentservice.dto.PaymentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumPaymentResponseDto {
    private PremiumRequestDto premiumRequest;
    private PaymentResponseDto paymentResponse;
    private boolean byUser;
}
