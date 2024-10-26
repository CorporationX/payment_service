package faang.school.paymentservice.publisher;

import faang.school.paymentservice.dto.event.dmsevent.DmsEventDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class DmsEventPublisher extends AbstractEventPublisher<DmsEventDto> {

    public DmsEventPublisher(
        @Qualifier("dmsTopic") ChannelTopic topic,
        @Qualifier("dmsEventRedisTemplate") RedisTemplate<String, DmsEventDto> redisTemplate
    ) {
        super(topic, redisTemplate);
    }
}
