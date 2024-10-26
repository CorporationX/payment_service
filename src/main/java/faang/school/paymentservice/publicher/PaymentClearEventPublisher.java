package faang.school.paymentservice.publicher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class PaymentClearEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic paymentClearTopic;

    public void publish(Object message) {
        redisTemplate.convertAndSend(paymentClearTopic.getTopic(), message);
    }
}
