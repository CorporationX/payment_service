package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeRateResponse(
        boolean success,
        long timestamp,
        String base,
        String date,
        Map<String, Double> rates,
        Boolean historical
) {
}
