package faang.school.paymentservice.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
public abstract class AbstractCacheManager<T> {

    protected final ObjectMapper mapper;
    protected final RedisTemplate<String, Object> redisTemplate;

    protected void put(String key, Map<String, T> values) {
        clear(key);
        this.redisTemplate.opsForHash().putAll(key, values);
    }

    protected T get(String key, String hashKey) {
        return (T) this.redisTemplate.opsForHash().get(key, hashKey);
    }

    private void clear(String key) {
        this.redisTemplate.delete(key);
    }
}
