package faang.school.paymentservice.service;

import faang.school.paymentservice.client.OpenExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.exception.NoSuchExchangeRateException;
import faang.school.paymentservice.exception.OpenExchangeRatesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRatesServiceTest {
    @Mock
    private OpenExchangeRatesClient openExchangeRatesClient;
    @InjectMocks
    private ExchangeRatesService exchangeRatesService;

    private Currency paymentCurrency;
    private Currency targetCurrency;

    @BeforeEach
    void setUp() {
        paymentCurrency = Currency.USD;
        targetCurrency = Currency.EUR;
    }

    @Test
    void testGetExchangeRate_Success() {
        BigDecimal rate = new BigDecimal("0.95");
        ExchangeRatesResponse response = new ExchangeRatesResponse();
        response.setRates(Map.of(targetCurrency, rate));

        when(openExchangeRatesClient.getExchangeRates(paymentCurrency, targetCurrency))
                .thenReturn(response);

        BigDecimal actual = exchangeRatesService.getExchangeRate(paymentCurrency, targetCurrency);

        assertEquals(rate, actual);
    }

    @Test
    void testGetExchangeRate_ShouldThrowExceptionWhenExchangeRateNotFound() {
        ExchangeRatesResponse response = new ExchangeRatesResponse();
        response.setRates(Map.of());
        when(openExchangeRatesClient.getExchangeRates(paymentCurrency, targetCurrency))
                .thenReturn(response);

        assertThrows(NoSuchExchangeRateException.class,
                () -> exchangeRatesService.getExchangeRate(paymentCurrency, targetCurrency));
    }

    @Test
    void testGetExchangeRate_ShouldThrowExceptionWhenOpenExchangeRatesIsNotAvailable() {
        when(openExchangeRatesClient.getExchangeRates(paymentCurrency, targetCurrency))
                .thenThrow(new OpenExchangeRatesException("Failed to get exchange rates from OpenExchangeRates!"));

        assertThrows(OpenExchangeRatesException.class,
                () -> exchangeRatesService.getExchangeRate(paymentCurrency, targetCurrency));
    }
}