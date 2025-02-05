package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyRateConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.exception.CurrencyRateException;
import faang.school.paymentservice.repository.CurrencyRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceTest {
    private Map<Currency, Double> rates;

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @Mock
    private CurrencyRateConfig rateConfig;

    @InjectMocks
    private CurrencyRateService currencyRateService;

    @Test
    void testSave() {
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRateService.save(currencyRate);
        verify(currencyRateRepository).saveCurrencyRate(currencyRate);
    }

    @BeforeEach
    void setUp() {
        rates = new HashMap<>();
        rates.put(Currency.USD, 1.0);
        rates.put(Currency.EUR, 0.85);
    }

    @Test
    void testExchangeCorrectInput() {
        when(currencyRateRepository.getCurrencyRates(Currency.USD, Currency.EUR)).thenReturn(rates);
        when(rateConfig.getConversionRateFactor()).thenReturn(1.0);

        BigDecimal amount = new BigDecimal("100");
        BigDecimal expectedResult = amount.multiply(BigDecimal.valueOf(0.85)).setScale(2, RoundingMode.HALF_UP);

        double result = currencyRateService.exchange(Currency.USD, Currency.EUR, amount);

        assertEquals(expectedResult.doubleValue(), result);
    }

    @Test
    void testExchangeFromRateIsZero() {
        rates.put(Currency.AED, 0.0);
        when(currencyRateRepository.getCurrencyRates(Currency.AED, Currency.EUR)).thenReturn(rates);
        when(rateConfig.getConversionRateFactor()).thenReturn(1.0);

        BigDecimal amount = new BigDecimal("100");

        assertThrows(CurrencyRateException.class, () -> currencyRateService.exchange(Currency.AED, Currency.EUR, amount));
    }

    @Test
    void testExchangeToRateIsZero() {
        when(currencyRateRepository.getCurrencyRates(Currency.USD, Currency.EUR)).thenReturn(rates);
        when(rateConfig.getConversionRateFactor()).thenReturn(1.0);

        BigDecimal amount = new BigDecimal("100");

        assertDoesNotThrow(() -> currencyRateService.exchange(Currency.USD, Currency.EUR, amount));
    }

    @Test
    void testExchange_CurrencyRateIsNull() {
        when(currencyRateRepository.getCurrencyRates(Currency.USD, Currency.EUR)).thenReturn(null);

        BigDecimal amount = new BigDecimal("100");

        assertThrows(NullPointerException.class, () -> currencyRateService.exchange(Currency.USD, Currency.EUR, amount));
    }
}