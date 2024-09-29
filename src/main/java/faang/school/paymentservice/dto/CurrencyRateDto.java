package faang.school.paymentservice.dto;

import java.util.Map;

public record CurrencyRateDto(
    String base,
    Map<Currency, Double> rates
) {}
