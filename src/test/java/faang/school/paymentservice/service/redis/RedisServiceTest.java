package faang.school.paymentservice.service.redis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension .class)
public class RedisServiceTest {

    @Mock
    private RedisTemplate<String, Double> redisTemplate;

    @Mock
    private ValueOperations<String, Double> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @Test
    void saveTest() {
        String key = "EUR/RUB";
        Double value = 100.0;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisService.save(key, value);

        verify(redisTemplate.opsForValue()).set(key, value);
    }

    @Test
    void getValueFromRedisTest() {
        String key = "EUR/RUB";
        Double value = 100.0;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(key)).thenReturn(value);

        Double actualValue = redisService.get(key);

        assertEquals(value, actualValue);
    }
}
