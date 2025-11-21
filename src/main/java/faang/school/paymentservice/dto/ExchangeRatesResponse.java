package faang.school.paymentservice.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ExchangeRatesResponse(
        String disclaimer,
        String license,
        long timestamp,
        String base,
        Map<String, BigDecimal> rates
) {}
