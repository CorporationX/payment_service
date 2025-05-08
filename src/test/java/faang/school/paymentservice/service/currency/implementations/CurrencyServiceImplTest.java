package faang.school.paymentservice.service.currency.implementations;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponse;
import faang.school.paymentservice.service.currency.interfaces.ExchangeRateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private ExchangeRateProvider provider;

    private CurrencyServiceImpl currencyService;

    @BeforeEach
    void setUp() {
        currencyService = new CurrencyServiceImpl(cacheManager, List.of(provider));
    }

    @Test
    void testFetchExchangeRatesWhenReturnRatesAndCacheBase() {
        Map<Currency, BigDecimal> rates = Map.of(
                Currency.EUR, new BigDecimal("1.0"),
                Currency.USD, new BigDecimal("0.9")
        );

        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .base("EUR")
                .rates(rates)
                .build();

        when(provider.getExchangeRates()).thenReturn(response);
        when(cacheManager.getCache("base")).thenReturn(cache);

        Map<Currency, BigDecimal> result = currencyService.fetchExchangeRates();

        assertEquals(rates, result);
        verify(cache).put("base", "EUR");
    }

    @Test
    void testGetExchangeRatesWhenReturnRatesByCallingFetch() {
        Map<Currency, BigDecimal> rates = Map.of(
                Currency.EUR, new BigDecimal("1.0")
        );

        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .base("EUR")
                .rates(rates)
                .build();

        when(provider.getExchangeRates()).thenReturn(response);
        when(cacheManager.getCache("base")).thenReturn(cache);

        Map<Currency, BigDecimal> result = currencyService.getExchangeRates();

        assertEquals(rates, result);
    }

    @Test
    void testGetBaseCurrency_WhenReturnCurrencyWithRateOne() {
        Map<Currency, BigDecimal> rates = Map.of(
                Currency.EUR, BigDecimal.ONE,
                Currency.USD, new BigDecimal("0.9")
        );

        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .base("EUR")
                .rates(rates)
                .build();

        when(provider.getExchangeRates()).thenReturn(response);
        when(cacheManager.getCache("base")).thenReturn(cache);

        Currency baseCurrency = currencyService.getBaseCurrency();

        assertEquals(Currency.EUR, baseCurrency);
    }
}