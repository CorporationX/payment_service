package faang.school.paymentservice.dto.exchange;

import faang.school.paymentservice.validator.currency.CurrencyValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyResponse {
    private Map<String, Double> rates;

    public BigDecimal getRate(String currency) {
        CurrencyValidator currencyValidator = new CurrencyValidator();
        currencyValidator.validateCurrencyResponse(this, currency);
        return BigDecimal.valueOf(rates.get(currency));
    }
}
