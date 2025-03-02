package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.math.RoundingMode;

@ExtendWith(MockitoExtension.class)
public class CurrencyConverterServiceTest {
    @Mock
    private ExchangeServiceClient exchangeServiceClient;

    private CurrencyConverterService currencyConverterService;

    private String token = "token";

    private double commissionRate = 1.5;

    @BeforeEach
    public void setUp() {
        currencyConverterService = new CurrencyConverterService(exchangeServiceClient, token, commissionRate);
    }

    @Test
    void testConvertCurrency() {
        Currency fromCurrency = Currency.USD;
        Currency toCurrency = Currency.EUR;
        BigDecimal amount = BigDecimal.valueOf(1000);

        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);

        ExchangeResponse response = new ExchangeResponse("disclaimer", "license", System.currentTimeMillis(), Currency.USD.name(), rates);

        when(exchangeServiceClient.exchange(token)).thenReturn(response);

        BigDecimal result = currencyConverterService.convertCurrency(fromCurrency, toCurrency, amount);

        BigDecimal expectedRate = BigDecimal.valueOf(rates.get(toCurrency.name()))
                .divide(BigDecimal.valueOf(rates.get(fromCurrency.name())), RoundingMode.HALF_UP);
        BigDecimal expectedConvertedAmount = amount.multiply(expectedRate);
        BigDecimal expectedCommission = expectedConvertedAmount.multiply(BigDecimal.valueOf(commissionRate)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal expectedAmount = expectedConvertedAmount.add(expectedCommission);

        assertEquals(expectedAmount, result);
    }

    @Test
    void testConvertCurrencyRateNotExist() {
        Currency fromCurrency = Currency.RUB;
        Currency toCurrency = Currency.EUR;
        BigDecimal amount = BigDecimal.valueOf(1000);

        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);

        ExchangeResponse response = new ExchangeResponse("disclaimer", "license", System.currentTimeMillis(), Currency.USD.name(), rates);

        when(exchangeServiceClient.exchange(token)).thenReturn(response);

        assertThrows(IllegalArgumentException.class, () -> {
            currencyConverterService.convertCurrency(fromCurrency, toCurrency, amount);
        }, String.format("Exchange rate not found for specified currencies: %s to %s", fromCurrency.name(), toCurrency.name()));
    }
}