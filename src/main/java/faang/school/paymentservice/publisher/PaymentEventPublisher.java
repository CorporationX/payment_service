package faang.school.paymentservice.publisher;

import faang.school.paymentservice.dto.event.payment.PaymentEvent;
import faang.school.paymentservice.mapper.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher extends AbstractPublisher<PaymentEvent> {
    public PaymentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 @Value("${spring.data.redis.channels.payment_channel.name}") String channelName,
                                 JsonMapper<PaymentEvent> jsonMapper) {
        super(redisTemplate, channelName, jsonMapper);
    }
}