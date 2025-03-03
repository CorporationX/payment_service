package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.paymentservice.properties.ExchangeServiceProperties;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.math.RoundingMode;

@ExtendWith(MockitoExtension.class)
public class CurrencyConverterServiceTest {
    @Mock
    private ExchangeServiceClient exchangeServiceClient;

    @Mock
    private ExchangeServiceProperties exchangeServiceProperties;

    @InjectMocks
    private CurrencyConverterServiceImpl currencyConverterService;

    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal amount;
    private Map<String, Double> rates;

    @BeforeEach
    public void setUp() {
        prepareData();
        Mockito.lenient().when(exchangeServiceProperties.getToken()).thenReturn("token");
        Mockito.lenient().when(exchangeServiceProperties.getCommissionRate()).thenReturn(1.5);
    }

    @Test
    void testConvertCurrency() {


        ExchangeResponse response = new ExchangeResponse("disclaimer", "license", System.currentTimeMillis(), Currency.USD.name(), rates);

        when(exchangeServiceClient.exchange(any())).thenReturn(response);

        BigDecimal result = currencyConverterService.convertCurrency(fromCurrency, toCurrency, amount);

        BigDecimal expectedRate = BigDecimal.valueOf(rates.get(toCurrency.name()))
                .divide(BigDecimal.valueOf(rates.get(fromCurrency.name())), RoundingMode.HALF_UP);
        BigDecimal expectedConvertedAmount = amount.multiply(expectedRate);
        BigDecimal expectedCommission = expectedConvertedAmount.multiply(BigDecimal.valueOf(1.5))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal expectedAmount = expectedConvertedAmount.add(expectedCommission);

        assertEquals(expectedAmount, result);
    }

    @Test
    void testConvertCurrencyRateNotExist() {
        rates.remove(toCurrency.name());

        ExchangeResponse response = new ExchangeResponse("disclaimer", "license", System.currentTimeMillis(), Currency.USD.name(), rates);

        when(exchangeServiceClient.exchange(any())).thenReturn(response);

        assertThrows(IllegalArgumentException.class, () -> {
            currencyConverterService.convertCurrency(fromCurrency, toCurrency, amount);
        }, String.format("Exchange rate not found for specified currencies: %s to %s", fromCurrency.name(), toCurrency.name()));
    }

    private void prepareData() {
        fromCurrency = Currency.USD;
        toCurrency = Currency.EUR;
        amount = BigDecimal.valueOf(1000);

        rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);
    }
}