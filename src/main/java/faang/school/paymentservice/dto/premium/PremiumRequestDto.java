package faang.school.paymentservice.dto.premium;


import faang.school.paymentservice.dto.CurrencyDto;
import faang.school.paymentservice.enums.PremiumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumRequestDto {
    private PremiumType premiumType;
    private Long userId;
    private CurrencyDto selectedCurrency;
    private boolean autoRenew;
}
