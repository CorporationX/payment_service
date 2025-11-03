package faang.school.paymentservice.dto.currency_converter;

import java.math.BigDecimal;
import java.util.Map;

public record LatestExchangeRatesResponse(
        String disclaimer,
        String license,
        Long timestamp,
        String base,
        Map<String, BigDecimal> rates
) {
}
