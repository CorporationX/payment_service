package faang.school.paymentservice.dto;

import java.util.Map;

public record ExchangeRatesResponse(
        String base,
        Map<String, Double> rates
) {}
