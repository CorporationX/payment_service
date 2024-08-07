package faang.school.paymentservice.service.exchange;

import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import faang.school.paymentservice.exception.CurrencyConversionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeServiceTest {
    @Mock
    private CurrencyClient currencyClient;
    @InjectMocks
    private ExchangeService exchangeService;
    private String appId = "appId";
    private BigDecimal commission = BigDecimal.valueOf(1.01);
    private Long paymentNumber = 1L;
    private BigDecimal amount = BigDecimal.valueOf(100);
    private Double rate = 0.9;
    private String baseCurrency = "USD";
    private String targetCurrency = "EUR";
    private PaymentRequest dto = new PaymentRequest(paymentNumber, amount, Currency.EUR);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(exchangeService, "baseCurrency", baseCurrency);
        ReflectionTestUtils.setField(exchangeService, "commission", commission);
        ReflectionTestUtils.setField(exchangeService, "appId", appId);
    }

    @Test
    public void testGetAmountInBaseCurrency() {
        Map<String, Double> rates = new HashMap<>();
        rates.put(targetCurrency, rate);
        CurrencyResponse currencyResponse = new CurrencyResponse(rates);

        when(currencyClient.getCurrencyRates(appId, baseCurrency, targetCurrency)).thenReturn(currencyResponse);

        BigDecimal result = exchangeService.getAmountInBaseCurrency(dto);
        BigDecimal expected = amount.divide(BigDecimal.valueOf(rate), MathContext.DECIMAL128);
        expected = expected.multiply(commission);

        assertEquals(expected.setScale(2, RoundingMode.HALF_UP), result);
    }

    @Test
    public void testGetAmountInBaseCurrencyWithNullCurrency() {
        Map<String, Double> rates = new HashMap<>();
        CurrencyResponse currencyResponse = new CurrencyResponse(rates);

        when(currencyClient.getCurrencyRates(appId, baseCurrency, targetCurrency)).thenReturn(currencyResponse);

        CurrencyConversionException e = assertThrows(
                CurrencyConversionException.class, () -> exchangeService.getAmountInBaseCurrency(dto));

        assertEquals("No exchange rate available for " + targetCurrency, e.getMessage());
    }

    @Test
    public void testIsCurrencyBase() {
        assertFalse(exchangeService.isCurrencyBase(dto));
    }
}
