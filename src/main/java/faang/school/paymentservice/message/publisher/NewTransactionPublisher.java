package faang.school.paymentservice.message.publisher;

import faang.school.paymentservice.event.NewTransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewTransactionPublisher implements MessagePublisher<NewTransactionEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic newTransactionTopic;

    @Override
    public void publish(NewTransactionEvent event) {
        redisTemplate.convertAndSend(newTransactionTopic.getTopic(), event);
        log.info("Published new transaction event: {}", event);
    }
}