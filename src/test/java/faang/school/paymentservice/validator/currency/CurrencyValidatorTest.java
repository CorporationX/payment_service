package faang.school.paymentservice.validator.currency;

import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import faang.school.paymentservice.exception.CurrencyConversionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CurrencyValidatorTest {
    @InjectMocks
    private CurrencyValidator currencyValidator;
    private Map<String, Double> rates = new HashMap<>();
    private String currency = "EUR";
    private String wrongCurrency = "USD";
    private Double rate = 0.9;
    private CurrencyResponse currencyResponse = new CurrencyResponse();

    @Test
    public void testValidateCurrencyResponseValid() {
        rates.put(currency, rate);
        currencyResponse.setRates(rates);

        assertDoesNotThrow(() -> currencyValidator.validateCurrencyResponse(currencyResponse, currency));
    }

    @Test
    public void testValidateCurrencyResponseNotValid() {
        rates.put(currency, rate);
        currencyResponse.setRates(rates);

        CurrencyConversionException e = assertThrows(CurrencyConversionException.class,
                () -> currencyValidator.validateCurrencyResponse(currencyResponse, wrongCurrency));

        assertEquals("No exchange rate available for " + wrongCurrency, e.getMessage());
    }

    @Test
    public void testValidateCurrencyResponseWithNullRates() {
        currencyResponse.setRates(rates);

        CurrencyConversionException e = assertThrows(CurrencyConversionException.class,
                () -> currencyValidator.validateCurrencyResponse(currencyResponse, wrongCurrency));

        assertEquals("No exchange rate available for " + wrongCurrency, e.getMessage());
    }
}
