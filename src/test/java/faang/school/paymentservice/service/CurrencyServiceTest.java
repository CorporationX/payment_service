package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRates;
import faang.school.paymentservice.dto.ExchangeRatesDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {
    @Mock
    ExchangeRates exchangeRates;

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    CurrencyService currencyService;

    @Test
    void currencyRateFetcher() {
        Map<String, String> rates = new HashMap<>();
        rates.put("USD", "5.3442");
        rates.put("RUB", "64.433");
        ExchangeRatesDto exchangeRatesDto = new ExchangeRatesDto();
        exchangeRatesDto.setBase("EUR");
        exchangeRatesDto.setDate(new Date());
        exchangeRatesDto.setTimestamp(2397293577L);
        exchangeRatesDto.setRates(rates);
        when(exchangeRates.fetchData().block()).thenReturn(exchangeRatesDto);
        // TODO create tests
    }
}