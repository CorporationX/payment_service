package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import faang.school.paymentservice.exception.CurrencyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CurrencyConverterServiceTest {

    @Mock
    private ExchangeRatesClient exchangeRatesClient;

    @InjectMocks
    private CurrencyConverterService currencyConverterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyConverterService = new CurrencyConverterService(exchangeRatesClient, "testAppId");
    }

    @Test
    void testConvertSuccess() {
        ExchangeRatesResponse response = new ExchangeRatesResponse();
        Map<String, Double> rates = Map.of("USD", 1.0, "KGS", 80.0);
        response.setRates(rates);

        when(exchangeRatesClient.getLatestRate("testAppId")).thenReturn(response);

        double result = currencyConverterService.convert("USD", "KGS", 100);

        assertEquals(100 * 80 * 1.01, result, 0.001);
    }

    @Test
    void testConvertCurrencyNotFound() {
        ExchangeRatesResponse response = new ExchangeRatesResponse();
        response.setRates(new HashMap<>());

        when(exchangeRatesClient.getLatestRate("testAppId")).thenReturn(response);

        assertThrows(CurrencyNotFoundException.class, () -> {
            currencyConverterService.convert("USD", "KGS", 100);
        });
    }
}