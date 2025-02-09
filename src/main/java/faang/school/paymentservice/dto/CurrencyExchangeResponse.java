package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrencyExchangeResponse(
        Long timeStamp,
        String base,
        Map<String, Double> rates
) {
    public BigDecimal getRate(Currency currency) {
        return BigDecimal.valueOf(rates.get(currency.name()));
    }
}
