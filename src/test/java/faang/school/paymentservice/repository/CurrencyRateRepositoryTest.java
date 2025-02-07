package faang.school.paymentservice.repository;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.exception.CurrencyRateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private CurrencyRateRepository currencyRateRepository;

    @Test
    void testSaveCurrencyRateThrowsCurrencyRateException() {
        CurrencyRate currencyRate = new CurrencyRate();

        assertThrows(CurrencyRateException.class, () ->
                currencyRateRepository.saveCurrencyRate(currencyRate));

        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    void testGetCurrencyRateThrowsCurrencyRateException() {
        when(redisTemplate.opsForHash()).thenThrow(RuntimeException.class);

        assertThrows(CurrencyRateException.class,
                () -> currencyRateRepository.getCurrencyRates(Currency.AFN, Currency.AMD));
    }

    @Test
    void testGetCreatedTimeThrowsCurrencyRateException() {
        when(redisTemplate.opsForHash()).thenThrow(RuntimeException.class);

        assertThrows(CurrencyRateException.class,
                () -> currencyRateRepository.getCreatedTime());
    }
}