package faang.school.paymentservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.retry.annotation.Retryable;

@RequiredArgsConstructor
@Slf4j
public class AbstractEventPublisher<T> {
    private final ChannelTopic topic;
    private final RedisTemplate<String, T> redisTemplate;

    @Retryable(
        retryFor = {RuntimeException.class},
        backoff = @Backoff(delayExpression = "${spring.data.redis.publisher.delay}")
    )
    public void publish(T event) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), event);
            log.debug("Event published: {}", event);
        } catch (Exception e) {
            log.error("Failing to publish event: {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
