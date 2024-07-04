package faang.school.paymentservice.publisher;

import faang.school.paymentservice.event.CancelPaymentEvent;
import faang.school.paymentservice.event.ClearPaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearPaymentPublisher implements MessagePublisher<ClearPaymentEvent> {

    @Value("${spring.data.channel.premium_bought.name}")
    private String topic;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(ClearPaymentEvent event) {
        redisTemplate.convertAndSend(topic, event);
        log.info("Published new cancel payment event: {}", event);
    }
}