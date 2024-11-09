package faang.school.paymentservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.model.event.PaymentEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher extends AbstractEventPublisher<PaymentEvent> {
    public PaymentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 @Qualifier("paymentChannelTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}