package faang.school.paymentservice.publisher;

import faang.school.paymentservice.dto.redis.RedisEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisEventPublisher {
    private final RedisTemplate<String, String> redisTemplate;

    public <T extends RedisEvent> void send(T event) {
        String topic = event.getChannelEvent();
        redisTemplate.convertAndSend(topic, event);
    }
}
