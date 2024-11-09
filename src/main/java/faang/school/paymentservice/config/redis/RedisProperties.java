package faang.school.paymentservice.config.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RedisProperties {
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.channel.payment-auth-event}")
    private String authTopic;

    @Value("${spring.data.redis.channel.payment-scheduled-event}")
    private String scheduledTopic;

    @Value("${spring.data.redis.channel.payment-cancel-event}")
    private String cancelTopic;

    @Value("${spring.data.redis.channel.payment-forced-event}")
    private String forcedTopic;

    @Value("${spring.data.redis.channel.response.auth-event-response}")
    private String authEventResponseTopic;

    @Value("${spring.data.redis.channel.response.scheduled-event-response}")
    private String scheduledEventResponseTopic;

    @Value("${spring.data.redis.channel.response.cancel-event-response}")
    private String cancelEventResponseTopic;

    @Value("${spring.data.redis.channel.response.forced-event-response}")
    private String forcedEventResponseTopic;
}
