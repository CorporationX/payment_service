package faang.school.paymentservice.dto;

import java.util.Map;

public record ExchangeRateResponse(
    String disclaimer,
    String license,
    long timestamp,
    String base,
    Map<String, Double> rates
) {
    public Double getRate(String currency) {
        return rates != null ? rates.get(currency) : null;
    }
}
