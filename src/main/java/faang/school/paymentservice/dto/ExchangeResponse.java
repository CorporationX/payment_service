package faang.school.paymentservice.dto;

import java.util.Map;

public record ExchangeResponse(
        String disclaimer,
        String license,
        Long timestamp,
        String base,
        Map<String, Double> rates
) {
}