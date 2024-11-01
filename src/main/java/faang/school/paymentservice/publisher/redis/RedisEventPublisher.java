package faang.school.paymentservice.publisher.redis;

import faang.school.paymentservice.dto.OperationMessage;
import faang.school.paymentservice.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
public class RedisEventPublisher implements EventPublisher<OperationMessage> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String topic;

    @Autowired
    public RedisEventPublisher(RedisTemplate<String, Object> redisTemplateWithJackson,
                               @Value("${spring.data.redis.channel.pending_operation}") String topic) {
        this.redisTemplate = redisTemplateWithJackson;
        this.topic = topic;
    }

    @Override
    public void publish(OperationMessage event) {
        redisTemplate.convertAndSend(topic, event);
        log.info("Event published to Redis topic {}: {}", topic, event);
    }
}
