package faang.school.paymentservice.config;

import faang.school.paymentservice.listener.redis.EventMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

@Configuration
public class RedisConfigListener {
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory,
                                                        List<EventMessageListener> listeners) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        listeners.forEach(listener -> container.addMessageListener(listener, listener.getTopic()));

        return container;
    }
}
