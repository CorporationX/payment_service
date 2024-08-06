package faang.school.paymentservice.dto.exchange;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CurrencyResponse {
    private Map<String, Double> rates;

    public BigDecimal getRate(String currency) {
        return BigDecimal.valueOf(rates.get(currency));
    }
}
