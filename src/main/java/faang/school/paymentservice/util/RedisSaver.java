package faang.school.paymentservice.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSaver {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${currency-rates.redis.ttl-hours}")
    private long ttlHours;

    public <T> void save(String key, T value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, ttlHours, TimeUnit.HOURS);
            log.info("Saved data in Redis: key='{}', value='{}'", key, jsonValue);
        } catch (Exception e) {
            log.error("Error occurred when trying to save data in Redis: {}", e.getMessage());
        }
    }
}