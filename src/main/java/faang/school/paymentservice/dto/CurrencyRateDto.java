package faang.school.paymentservice.dto;

import java.time.LocalDate;
import java.util.Map;

public record CurrencyRateDto(
        boolean success,
        Long timestamp,
        String base,
        LocalDate date,
        Map<String, Double> rates,
        Error error
) {}
