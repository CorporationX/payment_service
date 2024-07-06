package faang.school.paymentservice.publisher;

import faang.school.paymentservice.mapper.JsonMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@AllArgsConstructor
public abstract class AbstractPublisher<T> {
    private RedisTemplate<String, Object> redisTemplate;
    private String channelName;
    private JsonMapper<T> jsonMapper;

    public void publish(T object) {
        String json = jsonMapper.convertToJson(object);
        redisTemplate.convertAndSend(channelName, json);
    }
}