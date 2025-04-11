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
    private PremiumRequestDto premiumRequestDto;
    private PaymentResponseDto paymentResponseDto;
    private boolean byUser;
}
