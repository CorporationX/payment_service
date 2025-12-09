package faang.school.paymentservice.Service;

import faang.school.paymentservice.client.CurrencyRateFetcher;
import faang.school.paymentservice.dto.LatestRatesResponse;
import faang.school.paymentservice.service.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {
    @Mock
    private CurrencyRateFetcher currencyRateFetcher;

    @Mock
    private ReactiveRedisTemplate<String, LatestRatesResponse> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, LatestRatesResponse> valueOps;

    @InjectMocks
    private CurrencyServiceImpl service;

    private LatestRatesResponse fakeResponse;

    @BeforeEach
    void setUp() {
        fakeResponse = new LatestRatesResponse();
        fakeResponse.setSuccess(true);
        fakeResponse.setTimestamp(1719408000L);
        fakeResponse.setBase("EUR");
        fakeResponse.setDate("2025-11-25");
        fakeResponse.setRates(Map.of(
                "USD", 1.0712,
                "GBP", 0.8473,
                "JPY", 171.55));

        ReflectionTestUtils.setField(service, "redisKey", "latestRates");
        ReflectionTestUtils.setField(service, "baseCurrency", "EUR");

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(valueOps.set(anyString(), any(LatestRatesResponse.class))).thenReturn(Mono.just(true));
    }

    // ====================
    // Success Cases
    // ====================
    @Test
    @DisplayName("Get all rates from Redis should succeed")
    void testSuccessfulGetAllRatesFromRedis() {
        when(valueOps.get(anyString())).thenReturn(Mono.just(fakeResponse));

        StepVerifier.create(service.getAllRates())
                .expectNextMatches(response ->
                        response.getRates().get("USD").equals(1.0712) &&
                                response.getRates().get("GBP").equals(0.8473) &&
                                response.getRates().get("JPY").equals(171.55) &&
                                response.getBase().equals("EUR") &&
                                response.isSuccess()
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Get rate for existing currency should succeed")
    void testSuccessfulGetRateExistingCurrency() {
        when(valueOps.get(anyString())).thenReturn(Mono.just(fakeResponse));

        StepVerifier.create(service.getRate("USD"))
                .expectNext(1.0712)
                .verifyComplete();
    }

    // ====================
    // Error Cases
    // ====================
    @Test
    @DisplayName("Get rate for non-existing currency should error")
    void testGetRateNonExistingCurrency() {
        when(valueOps.get(anyString())).thenReturn(Mono.just(fakeResponse));

        StepVerifier.create(service.getRate("AUD"))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().contains("Currency AUD not found")
                )
                .verify();
    }

    @Test
    @DisplayName("Get all rates when Redis is empty and API returns empty should error")
    void testGetAllRatesWhileRedisIsEmptyAndApiReturnsEmpty() {
        when(valueOps.get(anyString())).thenReturn(Mono.empty());
        when(currencyRateFetcher.getLatestRates()).thenReturn(Mono.empty());

        StepVerifier.create(service.getAllRates())
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalStateException &&
                                throwable.getMessage().contains("Rates still not present in Redis after update")
                )
                .verify();
    }

    @Test
    @DisplayName("Get all rates when Redis is empty and API returns invalid response should error")
    void testGetAllRatesApiReturnsInvalid() {
        LatestRatesResponse badResponse = new LatestRatesResponse();
        badResponse.setSuccess(false);

        when(valueOps.get(anyString())).thenReturn(Mono.empty());
        when(currencyRateFetcher.getLatestRates()).thenReturn(Mono.just(badResponse));

        StepVerifier.create(service.getAllRates())
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalStateException ||
                                throwable instanceof IllegalArgumentException
                )
                .verify();
    }
}