package faang.school.paymentservice.service;

import faang.school.paymentservice.model.dto.ExchangeRatesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    ExchangeRates exchangeRates;

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    CurrencyService currencyService;

    private ExchangeRatesDto exchangeRatesDto;

    @BeforeEach
    void setUp() {
        exchangeRatesDto = new ExchangeRatesDto();
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void currencyRateFetcher_successfulResponse_savesRatesToRedis() {
        Map<String, String> rates = new HashMap<>();
        rates.put("USD", "5.3442");
        rates.put("RUB", "64.433");
        exchangeRatesDto.setSuccess("true");
        exchangeRatesDto.setBase("EUR");
        exchangeRatesDto.setDate(new Date());
        exchangeRatesDto.setTimestamp(2397293577L);
        exchangeRatesDto.setRates(rates);
        when(exchangeRates.fetchData()).thenReturn(Mono.just(exchangeRatesDto));

        currencyService.currencyRateFetcher();

        verify(valueOperations, times(1)).set("USD", "5.3442");
        verify(valueOperations, times(1)).set("RUB", "64.433");
        verify(exchangeRates, times(1)).fetchData();
    }

    @Test
    void currencyRateFetcher_unsuccessfulResponse_noInteractionWithRedis() {
        exchangeRatesDto.setSuccess(null);
        when(exchangeRates.fetchData()).thenReturn(Mono.empty());

        currencyService.currencyRateFetcher();

        verify(valueOperations, never()).set(anyString(), anyString());
        verify(exchangeRates, times(1)).fetchData();
    }
}