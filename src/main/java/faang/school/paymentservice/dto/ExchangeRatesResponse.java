package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ExchangeRatesResponse(
        @JsonProperty("result") String result,
        @JsonProperty("base_code") String baseCurrency,
        @JsonProperty("time_last_update_utc") String lastUpdate,
        @JsonProperty("rates") Map<String, Double> rates
) {
}
