package faang.school.paymentservice.dto;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ExchangeRatesDto(
        @JsonProperty("base") String baseCurrency,
        @JsonProperty("rates") Map<String, Double> rates
) {
}
