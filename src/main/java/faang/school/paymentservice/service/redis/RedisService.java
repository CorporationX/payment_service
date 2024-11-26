package faang.school.paymentservice.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Double> redisTemplate;

    public void save(String key, double value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Double get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}