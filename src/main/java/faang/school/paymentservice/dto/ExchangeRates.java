package faang.school.paymentservice.dto;

import java.util.Map;

public record ExchangeRates(
        Currency base,
        Map<String, Double> rates
) {
}
