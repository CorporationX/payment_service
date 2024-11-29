package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.mapper.CourseMapper;
import faang.school.paymentservice.dto.CourseDto;
import faang.school.paymentservice.dto.RatesDto;
import faang.school.paymentservice.service.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRateFetcher currencyRateFetcher;

    @Mock
    private RetryableCurrencyFetcher retryableCurrencyFetcher;

    @Spy
    private CourseMapper courseMapper;

    @Mock
    private RedisService redisService;

    private List<CourseDto> courses;

    @InjectMocks
    private CurrencyService currencyService;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<Double> doubleCaptor;

    @Test
    void updateRatesTest() {
        Map<String, Double> rates = new HashMap<>();
        String rub = "RUB";
        String eur = "EUR";
        String key = eur + "/" + rub;
        Double rate = 100.0;
        rates.put(rub, rate);
        RatesDto ratesDto = new RatesDto();
        ratesDto.setBase(eur);
        ratesDto.setRates(rates);
        when(retryableCurrencyFetcher.fetchDataWithRetry()).thenReturn(ratesDto);

        currencyService.updateRates();

        verify(redisService).save(stringCaptor.capture(), doubleCaptor.capture());
        String receivedKey = stringCaptor.getValue();
        Double receivedRate = doubleCaptor.getValue();
        assertEquals(key, receivedKey);
        assertEquals(rate, receivedRate);
    }

    @Test
    void convertTest() {
        String rub = "RUB";
        String eur = "EUR";
        String key = eur + "/" + rub;
        Long value = 10L;
        Double rate = 100.0;
        when(redisService.get(key)).thenReturn(rate);

        Double receivedValue = currencyService.convert(rub, value);

        assertEquals(value * rate, receivedValue);
    }

    @Test
    void convertNoRateInRedisTest() {
        when(redisService.get("EUR/RUB")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> currencyService.convert("RUB", 100L));
    }
}
