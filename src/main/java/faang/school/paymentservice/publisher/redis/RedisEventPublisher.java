package faang.school.paymentservice.publisher.redis;

import faang.school.paymentservice.dto.OperationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisEventPublisher implements EventPublisher<OperationMessage> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String topic;

    @Autowired
    public RedisEventPublisher(RedisTemplate<String, Object> redisTemplate,
                               @Value("${spring.data.redis.channel.pending_operation}") String topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(OperationMessage event) {
        redisTemplate.convertAndSend(topic, event);
        log.info("Event published to Redis topic {}: {}", topic, event);
    }
}
