package faang.school.paymentservice.dto;

import java.util.Map;

public record ExchangeRateResponseDto(
        String disclaimer,
        String license,
        String base,
        Map<String, Double> rates
) {
}
