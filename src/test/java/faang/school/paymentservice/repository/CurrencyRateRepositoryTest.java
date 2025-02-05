package faang.school.paymentservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.exception.CurrencyRateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateRepositoryTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CurrencyRateRepository currencyRateRepository;

    @Test
    void testSaveCurrencyRateThrowsJsonProcessingException() throws JsonProcessingException {
        CurrencyRate currencyRate = new CurrencyRate();
        when(objectMapper.writeValueAsString(currencyRate)).thenThrow(JsonProcessingException.class);

        assertThrows(CurrencyRateException.class, () ->
                currencyRateRepository.saveCurrencyRate(currencyRate));

        verify(objectMapper).writeValueAsString(currencyRate);
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    void testSaveCurrencyRateThrowsRuntimeException() throws JsonProcessingException {
        CurrencyRate currencyRate = new CurrencyRate();
        when(objectMapper.writeValueAsString(currencyRate)).thenThrow(RuntimeException.class);

        assertThrows(CurrencyRateException.class, () ->
                currencyRateRepository.saveCurrencyRate(currencyRate));

        verify(objectMapper).writeValueAsString(currencyRate);
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    void testGetCreatedTimeThrowsRuntimeException() {
        when(redisTemplate.opsForValue()).thenThrow(RuntimeException.class);

        assertThrows(CurrencyRateException.class, () -> currencyRateRepository.getCreatedTime());
    }
}