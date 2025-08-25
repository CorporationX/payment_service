package faang.school.paymentservice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.Rates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void cacheRates(String key, Rates rates) {
        try {
            String json = objectMapper.writeValueAsString(rates);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            log.error("Ошибка при сериализации курса валют", e);
        }
    }

    public Rates getCachedRates(String key) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Rates.class);
        } catch (Exception e) {
            log.error("Ошибка при десериализации курса валют", e);
            return null;
        }
    }
}
