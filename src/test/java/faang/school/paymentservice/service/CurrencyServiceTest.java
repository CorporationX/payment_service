package faang.school.paymentservice.service;

import faang.school.paymentservice.config.properties.CurrencyApiProperties;
import faang.school.paymentservice.config.properties.RetryProperties;
import faang.school.paymentservice.dto.CurrencyRatesResponse;
import faang.school.paymentservice.exception.CurrencyRateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {
    @InjectMocks
    private CurrencyService currencyService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient currencyWebClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private CurrencyApiProperties currencyApiProperties;

    @Mock
    private RetryProperties retryProperties;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private String baseCurrency;
    private String url;
    private String apiKey;
    private String redisKey;
    private int retryCount;
    private int retryDelay;
    private int readTimeout;
    private CurrencyRatesResponse currencyRatesResponse;

    @BeforeEach
    public void setUp() {
        url = "https://api.exchangeratesapi.io/v1/";
        apiKey = "test-api-key";
        redisKey = "currencyRates";
        baseCurrency = "EUR";
        readTimeout = 1000;
        retryCount = 2;
        retryDelay = 1000;

        currencyRatesResponse = new CurrencyRatesResponse();
        currencyRatesResponse.setSuccess(true);
        currencyRatesResponse.setBase(baseCurrency);
        currencyRatesResponse.setRates(new HashMap<>());

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testFetchAndSaveRates_Success() {

        CurrencyApiProperties.Timeout timeoutProperties = mock(CurrencyApiProperties.Timeout.class);
        when(timeoutProperties.getRead()).thenReturn(readTimeout);

        when(currencyApiProperties.getUrl()).thenReturn(url);
        when(currencyApiProperties.getApiKey()).thenReturn(apiKey);
        when(currencyApiProperties.getTimeout()).thenReturn(timeoutProperties);
        when(currencyApiProperties.getRedisKey()).thenReturn(redisKey);

        when(retryProperties.getRetry()).thenReturn(retryCount);
        when(retryProperties.getDelay()).thenReturn(retryDelay);

        when(currencyWebClient.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(CurrencyRatesResponse.class))
                .thenReturn(Mono.just(currencyRatesResponse));

        CurrencyRatesResponse result = currencyService.fetchAndSaveRates();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(baseCurrency, result.getBase());

        verify(redisTemplate.opsForValue(), times(1)).set(redisKey, currencyRatesResponse);
    }

    @Test
    public void testFetchAndSaveRates_Failure() {
        String errorMessage = "API error";

        CurrencyApiProperties.Timeout timeoutProperties = mock(CurrencyApiProperties.Timeout.class);
        when(timeoutProperties.getRead()).thenReturn(readTimeout);

        when(currencyApiProperties.getUrl()).thenReturn(url);
        when(currencyApiProperties.getApiKey()).thenReturn(apiKey);
        when(currencyApiProperties.getTimeout()).thenReturn(timeoutProperties);
        when(currencyApiProperties.getBaseCurrency()).thenReturn(baseCurrency);

        when(retryProperties.getRetry()).thenReturn(retryCount);
        when(retryProperties.getDelay()).thenReturn(retryDelay);

        when(currencyWebClient.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(CurrencyRatesResponse.class))
                .thenReturn(Mono.error(new RuntimeException(errorMessage)));

        CurrencyRateException exception = assertThrows(CurrencyRateException.class,
                () -> currencyService.fetchAndSaveRates());

        assertEquals("Temporary we accept only the following currencies: " + baseCurrency, exception.getMessage());

        verify(redisTemplate.opsForValue(), never()).set(anyString(), any());
    }

    @Test
    public void testGetCurrencyRatesFromRedis_Success() {

        when(currencyApiProperties.getRedisKey()).thenReturn(redisKey);
        when(valueOperations.get(redisKey)).thenReturn(currencyRatesResponse);

        CurrencyRatesResponse result = currencyService.getCurrencyRatesFromRedis();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(baseCurrency, result.getBase());

        verify(redisTemplate.opsForValue()).get(redisKey);
    }

    @Test
    public void testGetCurrencyRatesFromRedis_Failure_KeyAbsent() {
        // Arrange
        when(currencyApiProperties.getRedisKey()).thenReturn(redisKey);
        when(valueOperations.get(redisKey)).thenReturn(null);

        CurrencyApiProperties.Timeout timeoutProperties = mock(CurrencyApiProperties.Timeout.class);
        when(timeoutProperties.getRead()).thenReturn(readTimeout);

        when(currencyApiProperties.getUrl()).thenReturn(url);
        when(currencyApiProperties.getApiKey()).thenReturn(apiKey);
        when(currencyApiProperties.getTimeout()).thenReturn(timeoutProperties);
        when(currencyApiProperties.getRedisKey()).thenReturn(redisKey);

        when(retryProperties.getRetry()).thenReturn(retryCount);
        when(retryProperties.getDelay()).thenReturn(retryDelay);

        when(currencyWebClient.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(CurrencyRatesResponse.class))
                .thenReturn(Mono.just(currencyRatesResponse));

        CurrencyRatesResponse result = currencyService.getCurrencyRatesFromRedis();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(baseCurrency, result.getBase());

        verify(redisTemplate.opsForValue()).get(redisKey);
        verify(redisTemplate.opsForValue()).set(redisKey, currencyRatesResponse);
    }

    @Test
    public void testGetCurrencyRatesFromRedis_RedisUnavailable() {
        when(currencyApiProperties.getRedisKey()).thenReturn(redisKey);
        when(currencyApiProperties.getBaseCurrency()).thenReturn(baseCurrency);

        when(redisTemplate.opsForValue().get(redisKey)).thenThrow(new RuntimeException("Redis is unavailable"));

        CurrencyRateException exception = assertThrows(CurrencyRateException.class,
                () -> currencyService.getCurrencyRatesFromRedis());

        assertEquals("Temporary we accept only the following currencies: " + baseCurrency, exception.getMessage());

        verify(redisTemplate.opsForValue()).get(redisKey);
    }

    @Test
    public void testConvertToBaseCurrency_Success() {
        BigDecimal amount = new BigDecimal("100");
        String sourceCurrency = "RUB";
        BigDecimal exchangeRate = new BigDecimal("102.23");
        currencyRatesResponse.getRates().put(sourceCurrency.toUpperCase(), exchangeRate);

        when(currencyApiProperties.getRedisKey()).thenReturn(redisKey);
        when(valueOperations.get(redisKey)).thenReturn(currencyRatesResponse);

        BigDecimal result = currencyService.convertToBaseCurrency(amount, sourceCurrency);

        BigDecimal expected = amount.divide(exchangeRate, 5, RoundingMode.HALF_UP);
        assertEquals(expected, result);

        verify(redisTemplate.opsForValue()).get(redisKey);
    }

    @Test
    public void testConvertToBaseCurrency_ExchangeRateUnavailable() {
        BigDecimal amount = new BigDecimal("100");
        String sourceCurrency = "RRR";
        String errorMessage = String.format("Exchange rate for currency '%s' is not available. " +
                        "Available only following currencies: [USD]", sourceCurrency);
        currencyRatesResponse.getRates().put("USD", new BigDecimal("1.2"));

        when(currencyApiProperties.getRedisKey()).thenReturn(redisKey);
        when(valueOperations.get(redisKey)).thenReturn(currencyRatesResponse);

        CurrencyRateException exception = assertThrows(CurrencyRateException.class,
                () -> currencyService.convertToBaseCurrency(amount, sourceCurrency));

        assertEquals(errorMessage, exception.getMessage());

        verify(redisTemplate.opsForValue()).get(redisKey);
    }

    @Test
    public void testConvertToBaseCurrency_NoConversionRequired() {
        BigDecimal amount = new BigDecimal("100");
        String sourceCurrency = baseCurrency;
        when(currencyApiProperties.getBaseCurrency()).thenReturn(baseCurrency);

        BigDecimal result = currencyService.convertToBaseCurrency(amount, sourceCurrency);

        assertEquals(amount, result);

        verify(redisTemplate.opsForValue(), never()).get(anyString());
    }


}
