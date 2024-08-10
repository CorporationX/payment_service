package faang.school.paymentservice.validator.currency;

import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import faang.school.paymentservice.exception.CurrencyConversionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class CurrencyValidator {

    public void validateCurrencyResponse(CurrencyResponse currencyResponse, String currency) {
        Map<String, Double> rates = currencyResponse.getRates();
        if (rates == null || !rates.containsKey(currency) || rates.get(currency) == null) {
            log.error("No exchange rate available for " + currency);
            throw new CurrencyConversionException("No exchange rate available for " + currency);
        }
    }
}
