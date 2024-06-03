package faang.school.paymentservice.validator;

import faang.school.paymentservice.dto.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyValidatorTest {

    @Test
    public void testCheckExistCurrency() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("RUB", 90.0);

        CurrencyValidator currencyValidator = new CurrencyValidator();
        Currency fromCurrency = Currency.USD;
        Currency toCurrency = Currency.EUR;

        assertThrows(HttpMessageNotReadableException.class, () -> currencyValidator.checkExistCurrency(fromCurrency, toCurrency, rates));
    }
}