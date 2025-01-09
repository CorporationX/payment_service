package faang.school.paymentservice.publisher;

import faang.school.paymentservice.entity.Pending;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

public class PaymentEventPublisher extends AbstractEventPublisher<Pending> {
    public PaymentEventPublisher(RedisTemplate<String, Object> objectRedisTemplate, ChannelTopic paymentChannel) {
        super(objectRedisTemplate, paymentChannel);
    }

    @Override
    public Class getInstance() {
        return Pending.class;
    }
}
