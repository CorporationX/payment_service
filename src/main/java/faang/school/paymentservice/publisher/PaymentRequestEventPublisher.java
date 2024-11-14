package faang.school.paymentservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
@Slf4j
public class PaymentRequestEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic paymentRequestTopic;

    public void publish(Object message) {
        redisTemplate.convertAndSend(paymentRequestTopic.getTopic(), message);
        log.info("Message published: {}", message);
    }
}
