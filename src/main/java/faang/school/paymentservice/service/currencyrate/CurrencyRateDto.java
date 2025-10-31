package faang.school.paymentservice.service.currencyrate;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Map;

public record CurrencyRateDto(
        @JsonProperty("base")
        String baseCurrency,
        LocalDateTime date,
        Map<Currency, Double> rates
) {
}