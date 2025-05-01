package faang.school.paymentservice.dto.exchange;

import faang.school.paymentservice.dto.CurrencyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeResponseDto {
    private CurrencyDto currency;
    private BigDecimal amount;
    private Long userId;
}
