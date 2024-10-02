package faang.school.paymentservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record CurrencyRateDto(
    String date,
    String base,
    Map<Currency, Double> rates
) {}
