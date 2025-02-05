package faang.school.paymentservice.entity;

import faang.school.paymentservice.dto.Currency;
import lombok.Builder;

import java.util.Map;

@Builder
public record CurrencyRateDto(
        boolean success,
        Long timestamp,
        Currency base,
        String date,
        Map<Currency, Double> rates
) {}
