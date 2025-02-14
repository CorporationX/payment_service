package faang.school.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.payment.ExchangeRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {
    @Mock
    private ExchangeRatesClient exchangeRatesClient;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CurrencyService currencyService;

    private ExchangeRates cachedRates;
    private Map<String, Double> rates;

    @Value("${redis.channels.calculations-channel.name}")
    private String redisKey;

    @Value("${currency.exchange.actual-currency}")
    private String actualCurrency;

    @Value("${currency.exchange.access-key}")
    private String accessKey;


    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cachedRates = new ExchangeRates();
        rates = new HashMap<>();
        rates.put("EUR", 1.0);
        cachedRates.setRates(rates);
    }

    @Test
    public void testFetchCurrencyRates() {
        lenient().when(exchangeRatesClient.getExchangeRates(anyString(), anyString()))
                .thenThrow(new RuntimeException("FeignException"));

        assertDoesNotThrow(currencyService::fetchCurrencyRates);
        verify(redisTemplate.opsForValue(), never()).set(anyString(), any());
    }

    @Test
    public void testGetCurrencyRatesWithCache() {
        when(valueOperations.get(redisKey)).thenReturn(cachedRates);
        when(objectMapper.convertValue(cachedRates, ExchangeRates.class)).thenReturn(cachedRates);

        ExchangeRates result = currencyService.getCurrencyRates();

        assertNotNull(result);
        assertEquals(1.0, result.getRates().get("EUR"));
        verify(valueOperations).get(redisKey);
        verify(exchangeRatesClient, never()).getExchangeRates(anyString(), anyString());
    }

    @Test
    public void testGetCurrencyRatesNewRates() {
        when(valueOperations.get(redisKey)).thenReturn(null);
        when(exchangeRatesClient.getExchangeRates(accessKey, actualCurrency)).thenReturn(cachedRates);
        when(objectMapper.convertValue(any(), eq(ExchangeRates.class))).thenReturn(cachedRates);

        ExchangeRates result = currencyService.getCurrencyRates();

        assertNotNull(result);
        assertEquals(1.0, result.getRates().get("EUR"));
        verify(exchangeRatesClient).getExchangeRates(accessKey, actualCurrency);
        verify(valueOperations, times(2)).get(redisKey);
    }
}
