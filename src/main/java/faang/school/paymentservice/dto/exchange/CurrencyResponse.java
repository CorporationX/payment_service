package faang.school.paymentservice.dto.exchange;

import faang.school.paymentservice.exception.CurrencyConversionException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CurrencyResponse {
    private Map<String, Double> rates;

    public BigDecimal getRate(String currency) {
        if (rates == null || !rates.containsKey(currency) || rates.get(currency) == null) {
            log.error("No exchange rate available for " + currency);
            throw new CurrencyConversionException("No exchange rate available for " + currency);
        }
        return BigDecimal.valueOf(rates.get(currency));
    }
}
