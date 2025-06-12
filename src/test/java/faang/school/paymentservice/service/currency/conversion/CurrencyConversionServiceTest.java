package faang.school.paymentservice.service.currency.conversion;

import faang.school.paymentservice.client.CurrencyConverter.CurrencyConverterClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateDto;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.exception.CurrencyConversionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyConversionServiceTest {

    @Mock
    private CurrencyConverterClient currencyConverterClient;

    @InjectMocks
    private CurrencyConversionServiceImpl currencyConversionService;

    private Map<String, BigDecimal> rates;

    @BeforeEach
    public void setUp() {
        rates = new HashMap<>();
        rates.put("USD", BigDecimal.valueOf(1.00));
        rates.put("EUR", BigDecimal.valueOf(2.00));
        rates.put("GBP", BigDecimal.valueOf(3.00));

        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
        exchangeRateDto.setRates(rates);

        when(currencyConverterClient.getExchangeRates()).thenReturn(exchangeRateDto);
        currencyConversionService.initRates();
    }

    @Test
    public void testGetConvertedSum_WithUSD_ShouldReturnSameAmount() {
        PaymentRequest request = new PaymentRequest(1L, BigDecimal.valueOf(100), Currency.USD);
        BigDecimal result = currencyConversionService.getConvertedSum(request);

        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    public void testGetConvertedSum_WithEUR_ShouldApplyConversionAndFee() {
        PaymentRequest request = new PaymentRequest(2L, BigDecimal.valueOf(100), Currency.EUR);

        BigDecimal expected = BigDecimal.valueOf(100)
                .multiply(BigDecimal.valueOf(2.00))
                .multiply(BigDecimal.valueOf(0.99));

        BigDecimal result = currencyConversionService.getConvertedSum(request);

        assertEquals(expected, result);
    }

    @Test
    public void testGetConvertedSum_WithNullCurrency_ShouldThrowException() {
        PaymentRequest request = new PaymentRequest(3L, BigDecimal.valueOf(50), null);

        assertThrows(CurrencyConversionException.class, () -> {
            currencyConversionService.getConvertedSum(request);
        });
    }

    @Test
    public void testGetConvertedSum_WithUnsupportedCurrency_ShouldThrowException() {
        PaymentRequest invalidCurrencyRequest = new PaymentRequest(4L, BigDecimal.valueOf(10), null);

        assertThrows(CurrencyConversionException.class, () -> {
            currencyConversionService.getConvertedSum(invalidCurrencyRequest);
        });
    }
}
