package faang.school.paymentservice.listener.redis;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.Topic;

public interface EventMessageListener extends MessageListener {
    Topic getTopic();
}
