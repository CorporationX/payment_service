package faang.school.paymentservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record CurrencyRateDto(
    LocalDateTime updatedAt,
    String base,
    Map<Currency, Double> rates
) {}
