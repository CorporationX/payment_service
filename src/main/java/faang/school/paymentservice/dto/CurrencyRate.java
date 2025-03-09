package faang.school.paymentservice.dto;

import faang.school.paymentservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRate {
    private LocalDateTime timestamp;
    private Map<Currency, Double> rates;
}
