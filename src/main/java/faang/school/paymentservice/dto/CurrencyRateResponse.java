package faang.school.paymentservice.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record CurrencyRateResponse(
        String disclaimer,
        String license,
        long timestamp,
        String base,
        Map<String, Double> rates) {
}



