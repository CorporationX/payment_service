package faang.school.paymentservice.service.converter;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CurrencyConverterTest {

    @Mock
    private ExchangeRatesClient exchangeRatesClient;

    @InjectMocks
    private CurrencyConverter currencyConverter;

    @BeforeEach
    public void setUp() throws Exception {
        setPrivateField(currencyConverter, "appId", "test_app_id");
        setPrivateField(currencyConverter, "commission", BigDecimal.valueOf(1.05));
    }

    private void setPrivateField(Object targetObject, String fieldName, Object fieldValue) throws Exception {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, fieldValue);
    }

    @Test
    public void testConvert() {
        Currency fromCurrency = Currency.USD;
        Currency toCurrency = Currency.EUR;
        BigDecimal amount = BigDecimal.valueOf(100);

        ExchangeRatesResponse exchangeRatesResponse = new ExchangeRatesResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 2.0);
        rates.put("EUR", 0.85);
        exchangeRatesResponse.setRates(rates);

        doReturn(exchangeRatesResponse).when(exchangeRatesClient).getRates("test_app_id");

        BigDecimal result = currencyConverter.convert(fromCurrency, toCurrency, amount);

        BigDecimal expectedConversionRate = BigDecimal.valueOf(0.85 / 2.0);
        BigDecimal expected = expectedConversionRate.multiply(amount).multiply(BigDecimal.valueOf(1.05));
        assertEquals(expected, result);
    }
}
