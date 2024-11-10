package faang.school.paymentservice.publisher.redis;

import faang.school.paymentservice.dto.OperationMessage;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
public class RedisEventPublisher implements EventPublisher<OperationMessage> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<OperationStatus, String> channelMap;

    @Autowired
    public RedisEventPublisher(RedisTemplate<String, Object> redisTemplateWithJackson,
                               @Value("${spring.data.redis.channel.auth-payment-request}") String authChannel,
                               @Value("${spring.data.redis.channel.cancel-payment-request}") String cancelChannel,
                               @Value("${spring.data.redis.channel.clearing-payment-request}") String clearingChannel,
                               @Value("${spring.data.redis.channel.error-payment-request}") String errorChannel) {
        this.redisTemplate = redisTemplateWithJackson;
        this.channelMap = Map.of(
                OperationStatus.PENDING, authChannel,
                OperationStatus.CANCELLATION, cancelChannel,
                OperationStatus.CLEARING, clearingChannel,
                OperationStatus.ERROR, errorChannel
        );
    }

    @Override
    public void publish(OperationMessage event) {
        String channel = channelMap.get(event.getStatus());
        if (channel == null) {
            throw new IllegalArgumentException("No Redis channel configured for status: " + event.getStatus());
        }

        redisTemplate.convertAndSend(channel, event);
        log.info("Event published to Redis channel {}: {}", channel, event);
    }
}
